package pe.test.exchange.domain.model;

import lombok.Getter;
import pe.test.exchange.domain.exception.InvalidCurrencyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
public class Conversion {

    private static final int AMOUNT_SCALE = 2;

    private final Long id;
    private final BigDecimal amount;
    private final String sourceCurrency;
    private final String targetCurrency;
    private final BigDecimal exchangeRate;
    private final BigDecimal convertedAmount;
    private final LocalDateTime createdAt;


    public Conversion(BigDecimal amount, String sourceCurrency, String targetCurrency, BigDecimal exchangeRate) {
        validateCurrencies(sourceCurrency, targetCurrency);
        this.id = null;
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = amount.multiply(exchangeRate).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        this.createdAt = LocalDateTime.now();
    }


    public Conversion(Long id, BigDecimal amount, String sourceCurrency, String targetCurrency,
                      BigDecimal exchangeRate, BigDecimal convertedAmount, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = convertedAmount;
        this.createdAt = createdAt;
    }

    public static void validateCurrencies(String sourceCurrency, String targetCurrency) {
        if (sourceCurrency.equals(targetCurrency)) {
            throw new InvalidCurrencyException("La moneda de origen y la de destino deben ser diferentes");
        }
    }




}
