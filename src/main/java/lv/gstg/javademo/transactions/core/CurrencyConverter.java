package lv.gstg.javademo.transactions.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lv.gstg.javademo.transactions.external.currencyrates.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.math.RoundingMode.HALF_EVEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverter {

    @Value("@{app.baseCurrency}")
    String baseCurrency;

    private final ExchangeRateProvider rateProvider;

    //in-memory cache/storage of currency rates
    //represents quote to rate map, like USDJPY=147.439501, USDMXN=17.46935, ...
    private Map<String, BigDecimal> rates = initialRates();

    @SneakyThrows
    private Map<String, BigDecimal> initialRates() {
        Map<String, BigDecimal> rates = new HashMap<>();
        var mapper = new ObjectMapper();
        var tree = mapper.readTree(getClass().getClassLoader().getResourceAsStream("data/exchangerate/usd.json"));
        tree.findValue("quotes").fields()
                .forEachRemaining(entry -> rates.put(entry.getKey(), entry.getValue().decimalValue()));
        return Collections.unmodifiableMap(rates);
    }

    // first update 15 seconds after startup, then once every hour
    @Scheduled(initialDelay = 15, fixedRate = 60 * 60, timeUnit = TimeUnit.SECONDS)
    public void updateRates() {
        try {
            var rates = rateProvider.getLatestRates();
            this.rates = Map.copyOf(rates);
            log.info("Rates updated successful, {} entries stored", rates.size());
        } catch (Exception e) {
            log.warn("Failed to update rates: " + e.getMessage(), e);
        }
    }

    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        var rate = findRate(fromCurrency, toCurrency);
        return convertUsingRate(amount, rate);
    }

    public BigDecimal convertUsingRate(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(amount.scale(), HALF_EVEN);
    }

    void setRates(Map<String, BigDecimal> rates) {
        this.rates = Map.copyOf(rates);
    }

    public BigDecimal findRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency))
            return BigDecimal.ONE;

        var rate = rates.get(fromCurrency + toCurrency);
        if (rate != null)
            return rate;

        rate = rates.get(toCurrency + fromCurrency);
        if (rate != null)
            // return 1/rate
            return BigDecimal.ONE.divide(rate, 9, HALF_EVEN);

        // in order to calculate EUR/JPY rate we use USD/EUR and USD/JPY,
        // assuming rates have USD as base currency, and every supported currency XXX has USDXXX quote present
        var rateUsdToX = rates.get(baseCurrency + fromCurrency);
        var rateUsdToY = rates.get(baseCurrency + toCurrency);
        if (rateUsdToX == null)
            throw new IllegalArgumentException("Currency '" + fromCurrency + "' is not supported");
        if (rateUsdToY == null)
            throw new IllegalArgumentException("Currency '" + toCurrency + "' is not supported");

        return rateUsdToY.divide(rateUsdToX, 9, HALF_EVEN);
    }

}
