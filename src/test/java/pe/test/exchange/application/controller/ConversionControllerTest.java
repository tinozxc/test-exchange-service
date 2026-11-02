package pe.test.exchange.application.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.test.exchange.application.GlobalExceptionHandler;
import pe.test.exchange.application.dto.ConversionResponse;
import pe.test.exchange.application.service.ConversionService;
import pe.test.exchange.domain.exception.ExchangeServiceException;
import pe.test.exchange.domain.exception.InvalidCurrencyException;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ConversionControllerTest {

    private static final String VALID_BODY =
            "{\"monto\":100,\"monedaOrigen\":\"USD\",\"monedaDestino\":\"PEN\"}";

    @Mock
    private ConversionService conversionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ConversionController(conversionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returns200WithConversion() throws Exception {
        when(conversionService.convert(any())).thenReturn(new ConversionResponse(
                new BigDecimal("100"), new BigDecimal("370.00"), "USD", "PEN", new BigDecimal("3.70")));

        mockMvc.perform(post("/api/conversion").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montoConvertido").value(370.00))
                .andExpect(jsonPath("$.monedaDestino").value("PEN"));
    }

    @Test
    void returns400WhenAmountIsNegative() throws Exception {
        String body = "{\"monto\":-5,\"monedaOrigen\":\"USD\",\"monedaDestino\":\"PEN\"}";

        mockMvc.perform(post("/api/conversion").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400WhenCurrencyFormatIsInvalid() throws Exception {
        String body = "{\"monto\":100,\"monedaOrigen\":\"US\",\"monedaDestino\":\"PEN\"}";

        mockMvc.perform(post("/api/conversion").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/conversion").contentType(MediaType.APPLICATION_JSON).content("{oops"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400WhenCurrencyIsNotSupported() throws Exception {
        when(conversionService.convert(any()))
                .thenThrow(new InvalidCurrencyException("Currency not supported: ZZZ"));

        mockMvc.perform(post("/api/conversion").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void returns503WhenProviderFails() throws Exception {
        when(conversionService.convert(any())).thenThrow(new ExchangeServiceException("down"));

        mockMvc.perform(post("/api/conversion").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

}
