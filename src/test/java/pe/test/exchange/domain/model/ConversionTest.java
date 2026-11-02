package pe.test.exchange.domain.model;

import org.junit.jupiter.api.Test;
import pe.test.exchange.domain.exception.InvalidCurrencyException;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ConversionTest {

    @Test
    void calculatesConvertedAmountRoundedHalfUp() {
        Conversion conversion = new Conversion(
                new BigDecimal("100"), "USD", "PEN", new BigDecimal("3.7051"));

        assertThat(conversion.getConvertedAmount()).isEqualByComparingTo("370.51");
        assertThat(conversion.getCreatedAt()).isNotNull();
    }

    @Test
    void roundsHalfUpToTwoDecimals() {
        Conversion conversion = new Conversion(
                new BigDecimal("10.01"), "USD", "PEN", new BigDecimal("0.3333"));

        assertThat(conversion.getConvertedAmount()).isEqualByComparingTo("3.34");
    }

    @Test
    void rejectsSameCurrency() {
        assertThatThrownBy(() -> new Conversion(BigDecimal.TEN, "USD", "USD", BigDecimal.ONE))
                .isInstanceOf(InvalidCurrencyException.class);
    }

}
