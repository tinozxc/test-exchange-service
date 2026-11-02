package pe.test.exchange.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.domain.exception.ExchangeServiceException;
import pe.test.exchange.domain.exception.InvalidCurrencyException;
import pe.test.exchange.domain.model.Conversion;
import pe.test.exchange.domain.repository.ConversionRepository;
import pe.test.exchange.domain.service.ExchangeRateProvider;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversionServiceTest {

    @Mock
    private ExchangeRateProvider rateProvider;

    @Mock
    private ConversionRepository conversionRepository;

    @InjectMocks
    private ConversionService conversionService;

    @Test
    void convertsAndSavesConversion() {
        when(rateProvider.getRate("USD", "PEN")).thenReturn(new BigDecimal("3.7051"));

        ConversionResponse response = conversionService.convert(
                new ConversionRequest(new BigDecimal("100"), "usd", "pen"));

        assertThat(response.montoConvertido()).isEqualByComparingTo("370.51");
        assertThat(response.tipoCambio()).isEqualByComparingTo("3.7051");
        assertThat(response.monedaOrigen()).isEqualTo("USD");

        ArgumentCaptor<Conversion> captor = ArgumentCaptor.forClass(Conversion.class);
        verify(conversionRepository).save(captor.capture());
        assertThat(captor.getValue().getConvertedAmount()).isEqualByComparingTo("370.51");
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void rejectsSameCurrencyWithoutCallingProvider() {
        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(BigDecimal.TEN, "USD", "usd")))
                .isInstanceOf(InvalidCurrencyException.class);

        verifyNoInteractions(rateProvider, conversionRepository);
    }

    @Test
    void doesNotSaveWhenProviderFails() {
        when(rateProvider.getRate("USD", "PEN")).thenThrow(new ExchangeServiceException("down"));

        assertThatThrownBy(() -> conversionService.convert(
                new ConversionRequest(BigDecimal.TEN, "USD", "PEN")))
                .isInstanceOf(ExchangeServiceException.class);

        verify(conversionRepository, never()).save(any());
    }

}
