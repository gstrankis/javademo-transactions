package lv.gstg.javademo.transactions.external.currencyrates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * Currency rate provider backed by Currency Data API<br>
 * See <a href="https://exchangerate.host/documentation">exchangerate API docs</a>
 * <br>
 * Alternatives: <a href="https://apilayer.com/marketplace/currency_data-api#documentation-tab">APILayer API</a>
 */
@Component
@Slf4j
public class ExchangeRateProvider {

    @Value("${app.external.exchangerate.baseUrl}")
    String baseUrl;

    @Value("${app.external.exchangerate.key}")
    String key;

    @Value("${app.external.exchangerate.sources}")
    String sourceCurrencies;

    @Value("${app.currencies}")
    String currencies;

    private RestClient restClient;
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Retrieves data from api.exchangerate.host/live<br>
     * returns map of quotes corresponding 'quotes' node of the data retrieved
     * <pre>"quotes":{"USDEUR":0.908575,"USDGBP":0.78677,"USDJPY":147.36801, ...}</pre>
     */
    Map<String, BigDecimal> getLiveQuotes(String sourceCurrency) {
        var uri = buildLiveUri(sourceCurrency);
        try {
            var start = currentTimeMillis();
            var response = restClient().get().uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
            log.info("Got response in {} ms from {}/live: {} ", currentTimeMillis() - start, baseUrl, abbreviate(response, 100));

            JsonNode responseRoot = mapper.readTree(response);
            if (responseRoot.findValue("success") == null || !responseRoot.findValue("success").asBoolean())
                throw new ExchangeRateException("success=false, response=" + response);
            return parseLiveResponse(responseRoot);
        } catch (RestClientException e) {
            throw new ExchangeRateException(MessageFormat.format("Failed to get data from {0}: {1}", uri, e.getMessage()), e);
        } catch (JsonProcessingException | ExchangeRateException e) {
            throw new ExchangeRateException(MessageFormat.format("Failed to process data retrieved: {0} ", e.getMessage()), e);
        }
    }

    /**
     * Transforms 'quotes' node of the response json to map of quotes
     * <pre>"quotes":{"USDEUR":0.908575,"USDGBP":0.78677,"USDJPY":147.36801, ...}</pre>
     */
    Map<String, BigDecimal> parseLiveResponse(JsonNode responseRoot) throws JsonProcessingException {
        Map<String, BigDecimal> result = new HashMap<>();
        responseRoot.findValue("quotes").fields().forEachRemaining(entry -> result.put(entry.getKey(), entry.getValue().decimalValue()));
        return result;
    }

    String buildLiveUri(String sourceCurrency) {
        //builds an uri like this:
        //http://api.exchangerate.host/live?access_key=$EXCHANGERATE_KEY&source=USD&currencies=USD,EUR,GBP,JPY,CHF,CAD,MXN,PLN,SEK,NOK,RON,CZK,DKK
        var uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("live")
                .queryParam("access_key", key)
                .queryParam("source", sourceCurrency)
                .queryParam("currencies", currencies)
                .build()
                .encode().toUriString();
        return uri;
    }

    private RestClient restClient() {
        if (restClient != null)
            return restClient;
        return restClient = createRestClient();
    }

    private RestClient createRestClient() {
        var template = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        return RestClient.create(template);
    }

    /**
     * Retrieves exchange rates from the source, returns as 'quote to rate' map
     *
     * @return Map of quotes like 'USDSEUR' mapped to exchangerate as BigDecimal
     * @throws ExchangeRateException
     */
    public Map<String, BigDecimal> getLatestRates() throws ExchangeRateException {
        var result = new HashMap<String, BigDecimal>();
        Arrays.stream(sourceCurrencies.split(","))
                .forEach(sourceCurrency -> result.putAll(getLiveQuotes(sourceCurrency)));
        return result;
    }

    static class ExchangeRateException extends RuntimeException {
        ExchangeRateException(String message) {
            super(message);
        }

        public ExchangeRateException(Throwable cause) {
            super(cause);
        }

        public ExchangeRateException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
