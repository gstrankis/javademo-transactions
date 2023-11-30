package lv.gstg.javademo.transactions.external.currencyrates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test of ExchangeRateProvider <br>
 * requires valid environment variable EXCHANGERATE_KEY provided
 */
@SpringBootTest(classes = ExchangeRateProvider.class)
@ActiveProfiles("test")
public class ExchangeRateProviderIT {
    @Autowired
    ExchangeRateProvider provider;

    @Test
    void getLiveQuotes_USD() {
        Map<String, BigDecimal> result = provider.getLiveQuotes("USD");
        System.out.println("ExchangeRateProvider.getLiveQuotes(USD):\n" + result);
        assertThat(result)
                .hasSize(12)
                .containsKey("USDEUR");
    }

}
