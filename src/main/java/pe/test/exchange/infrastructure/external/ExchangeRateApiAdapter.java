package pe.test.exchange.infrastructure.external;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.test.exchange.domain.exception.ExchangeServiceException;
import pe.test.exchange.domain.exception.InvalidCurrencyException;
import pe.test.exchange.domain.service.ExchangeRateProvider;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

import java.math.BigDecimal;

@Component
public class ExchangeRateApiAdapter implements ExchangeRateProvider {

    private final ExchangeRateApiClient client;
    private final String apiKey;

    public ExchangeRateApiAdapter(ExchangeRateApiClient client,
                                  @Value("${exchange-rate.api.key}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public BigDecimal getRate(String sourceCurrency, String targetCurrency) {
        ExchangeRateApiResponse response = callApi(sourceCurrency);

        if (response == null || !"success".equals(response.result())) {
            if (response != null && "unsupported-code".equals(response.errorType())) {
                throw new InvalidCurrencyException("Moneda no admitida: " + sourceCurrency);
            }
            throw new ExchangeServiceException("El servicio de tipos de cambio ha devuelto un error");
        }

        BigDecimal rate = response.conversionRates() == null
                ? null
                : response.conversionRates().get(targetCurrency);

        if (rate == null || rate.signum() <= 0) {
            throw new InvalidCurrencyException("Moneda no admitida: " + targetCurrency);
        }
        return rate;
    }


    private ExchangeRateApiResponse callApi(String sourceCurrency) {
        try {
            return client.getLatestRates(apiKey, sourceCurrency);
        } catch (FeignException.NotFound e) {
            throw new InvalidCurrencyException("Moneda no admitida: " + sourceCurrency);
        } catch (FeignException e) {
            throw new ExchangeServiceException("El servicio de tipos de cambio no esta disponible", e);
        }
    }
}
