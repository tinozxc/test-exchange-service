package pe.test.exchange.infrastructure.external;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.test.exchange.domain.exception.ExchangeServiceException;
import pe.test.exchange.domain.exception.InvalidCurrencyException;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiAdapterTest {

    private static final String API_KEY = "api-key-test";

    @Mock
    private ExchangeRateApiClient client;

    private ExchangeRateApiAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ExchangeRateApiAdapter(client, API_KEY);
    }

    @Test
    void returnsRateWhenResponseIsSuccessful() {
        when(client.getLatestRates(API_KEY, "USD")).thenReturn(new ExchangeRateApiResponse(
                "success", null, "USD", Map.of("PEN", new BigDecimal("3.7051"))));

        assertThat(adapter.getRate("USD", "PEN")).isEqualByComparingTo("3.7051");
    }

    @Test
    void throwsInvalidCurrencyWhenProviderReportsUnsupportedCode() {
        when(client.getLatestRates(API_KEY, "XXX")).thenReturn(new ExchangeRateApiResponse(
                "error", "unsupported-code", null, null));

        assertThatThrownBy(() -> adapter.getRate("XXX", "PEN"))
                .isInstanceOf(InvalidCurrencyException.class);
    }

    @Test
    void throwsInvalidCurrencyWhenTargetRateIsMissing() {
        when(client.getLatestRates(API_KEY, "USD")).thenReturn(new ExchangeRateApiResponse(
                "success", null, "USD", Map.of("EUR", new BigDecimal("0.92"))));

        assertThatThrownBy(() -> adapter.getRate("USD", "ZZZ"))
                .isInstanceOf(InvalidCurrencyException.class);
    }

    @Test
    void throwsExchangeServiceExceptionWhenProviderReportsOtherError() {
        when(client.getLatestRates(API_KEY, "USD")).thenReturn(new ExchangeRateApiResponse(
                "error", "invalid-key", null, null));

        assertThatThrownBy(() -> adapter.getRate("USD", "PEN"))
                .isInstanceOf(ExchangeServiceException.class);
    }

    @Test
    void throwsExchangeServiceExceptionWhenResponseIsNull() {
        when(client.getLatestRates(API_KEY, "USD")).thenReturn(null);

        assertThatThrownBy(() -> adapter.getRate("USD", "PEN"))
                .isInstanceOf(ExchangeServiceException.class);
    }

    @Test
    void throwsExchangeServiceExceptionWhenClientFails() {
        when(client.getLatestRates(API_KEY, "USD")).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> adapter.getRate("USD", "PEN"))
                .isInstanceOf(ExchangeServiceException.class);
    }

}
