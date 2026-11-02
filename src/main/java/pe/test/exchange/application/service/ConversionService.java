package pe.test.exchange.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.test.exchange.application.dto.ConversionRequest;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.domain.model.Conversion;
import pe.test.exchange.domain.repository.ConversionRepository;
import pe.test.exchange.domain.service.ExchangeRateProvider;

import java.math.BigDecimal;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final ExchangeRateProvider rateProvider;
    private final ConversionRepository conversionRepository;

    public ConversionResponse convert(ConversionRequest request) {
        String source = request.monedaOrigen().toUpperCase(Locale.ROOT);
        String target = request.monedaDestino().toUpperCase(Locale.ROOT);

        Conversion.validateCurrencies(source, target);

        BigDecimal rate = rateProvider.getRate(source, target);

        Conversion conversion = new Conversion(request.monto(), source, target, rate);
        conversionRepository.save(conversion);

        return new ConversionResponse(
                conversion.getAmount(), conversion.getConvertedAmount(), source, target, rate);
    }

}
