package pe.test.exchange.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull(message = "monto is requerido")
        @Positive(message = "monto debe ser mayor a 0")
        BigDecimal monto,

        @NotBlank(message = "moneda de origen es requerida")
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "moneda de Origen debe ser código de tres letras")
        String monedaOrigen,

        @NotBlank(message = "moneda Destino es requerida")
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "moneda Destino debe ser  codigo de tres letras")
        String monedaDestino
) {
}

