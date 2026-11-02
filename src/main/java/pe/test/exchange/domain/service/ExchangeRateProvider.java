package pe.test.exchange.domain.service;

import java.math.BigDecimal;

public interface  ExchangeRateProvider {

    /**
    * @throws pe.test.exchange.domain.exception.ExchangeServiceException
    * @throws pe.test.exchange.domain.exception.InvalidCurrencyException
    */

    BigDecimal getReate(String sourceCurrency , String targetCurrency);
}
