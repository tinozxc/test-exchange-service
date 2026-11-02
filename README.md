# Exchange Service - API de Tipo de Cambio

Microservicio en Java que convierte montos entre monedas consumiendo [ExchangeRate-API](https://www.exchangerate-api.com/) y registra cada conversión en H2.

## Stack

Java 17 · Spring Boot 4.0.6 · Spring Cloud OpenFeign · Spring Data JPA · H2 · Maven

## Cómo ejecutar

Requisitos: JDK 17 e internet.

1. Crea el archivo `.env` en la raíz a partir de la plantilla y coloca tu API key:

```bash
cp .env.example .env          # Windows: copy .env.example .env
```

```
EXCHANGE_API_KEY=tu_api_key
```

2. Ejecuta:

```bash
./mvnw spring-boot:run        # Windows: .\mvnw.cmd spring-boot:run
./mvnw clean verify           # tests (no requieren .env)
```

La app levanta en `http://localhost:8080`. También se puede definir `EXCHANGE_API_KEY` como variable de entorno en lugar de usar `.env`.

## API

### POST /api/conversion

**Request**

```json
{ "monto": 100, "monedaOrigen": "USD", "monedaDestino": "PEN" }
```

**Response 200**

```json
{
  "monto": 100,
  "montoConvertido": 370.51,
  "monedaOrigen": "USD",
  "monedaDestino": "PEN",
  "tipoCambio": 3.7051
}
```

**Ejemplo de error (400)**

```json
{
  "status": 400,
  "message": "Source and target currency must be different",
  "timestamp": "2026-11-02T08:15:30.123"
}
```

| Caso | HTTP |
|---|---|
| Monto inválido, formato de moneda inválido, moneda no soportada, origen igual a destino, JSON malformado | 400 |
| Proveedor externo caído, timeout, key inválida o cuota agotada | 503 |
| Error inesperado | 500 |

## Base de datos H2

Consola: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, usuario `sa`, password `password`).

```sql
SELECT * FROM conversions;
```

## Arquitectura

Capas `domain`, `application` e `infrastructure` con puertos y adaptadores: el dominio define las interfaces y la infraestructura las implementa (Feign y JPA).

```
Controller -> ConversionService -> ExchangeRateProvider (puerto) <- ExchangeRateApiAdapter -> Feign
                                -> ConversionRepository (puerto)  <- ConversionRepositoryAdapter -> H2
```