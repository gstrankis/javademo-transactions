package lv.gstg.javademo.transactions.external.currencyrates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ExchangeRateProviderTest {

    ExchangeRateProvider provider = new ExchangeRateProvider();

    @Test
    @SneakyThrows
    void parseLiveResponse() {
        var node = readFromResource("currencyrates/exchangerate-live-usd.json");
        var result = provider.parseLiveResponse(node);
        assertThat(result)
                .hasSize(12)
                .containsEntry("USDEUR", new BigDecimal("0.908575"));
    }

    @SneakyThrows
    private JsonNode readFromResource(String resourceName) {
        var in = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        if (in == null)
            throw new IllegalArgumentException("no such resource " + resourceName);
        return new ObjectMapper().readTree(in);
    }

}
