package lv.gstg.javademo.transactions.external.currencyrates;

import org.junit.jupiter.api.Disabled;
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

    //TODO: configure gradle build to support separate 'integration tests' task
    //      See https://docs.gradle.org/current/samples/sample_jvm_multi_project_with_additional_test_types.html
    @Test
    @Disabled("Enable and execute on demand until 'integrationTest' task is properly configured")
    void getLiveQuotes_USD() {
        Map<String, BigDecimal> result = provider.getLiveQuotes("USD");
        System.out.println("ExchangeRateProvider.getLiveQuotes(USD):\n" + result);
        assertThat(result)
                .hasSize(12)
                .containsKey("USDEUR");
    }

}
