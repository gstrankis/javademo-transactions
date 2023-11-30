package lv.gstg.javademo.transactions.core;

import lv.gstg.javademo.transactions.external.currencyrates.ExchangeRateProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterTest {

    @Mock
    ExchangeRateProvider provider;

    @InjectMocks
    CurrencyConverter converter;

    @Test
    void convert() {
        converter.setRates(Map.of("USDEUR", new BigDecimal("0.9"), "USDJPY", new BigDecimal("150.0")));
        assertThat(converter.convert("USD", "EUR", new BigDecimal("1.00"))).isEqualByComparingTo("0.90");
        assertThat(converter.convert("EUR", "USD", new BigDecimal("1.00"))).isEqualByComparingTo("1.11");
        assertThat(converter.convert("EUR", "JPY", new BigDecimal("1.00"))).isEqualByComparingTo("166.67");
        assertThat(converter.convert("JPY", "EUR", new BigDecimal("1.00"))).isEqualByComparingTo("0.01");

        assertThat(converter.convert("USD", "EUR", new BigDecimal("1.01"))).isEqualByComparingTo("0.91");
        assertThat(converter.convert("USD", "EUR", new BigDecimal("1.00001"))).isEqualByComparingTo("0.90001");
        assertThat(converter.convert("JPY", "EUR", new BigDecimal("100.00"))).isEqualByComparingTo("0.60");
    }

    @Test
    void findRate() {
        converter.setRates(Map.of("USDEUR", new BigDecimal("0.9"), "USDJPY", new BigDecimal("150.0")));
        assertThat(converter.findRate("USD", "EUR")).isEqualByComparingTo("0.90");
        assertThat(converter.findRate("EUR", "USD")).isEqualByComparingTo("1.111111111");
        assertThat(converter.findRate("JPY", "EUR")).isEqualByComparingTo("0.006");
        assertThat(converter.findRate("EUR", "JPY")).isEqualByComparingTo("166.666666667");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> converter.findRate("USD", "XXX"));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> converter.findRate("XXX", "USD"));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> converter.findRate("XXX", "YYY"));
    }

    @Test
    void checkInitialRates() {
        //check converter has initial rates loaded
        assertThat(converter.findRate("USD", "EUR")).isNotNull();
    }
}
