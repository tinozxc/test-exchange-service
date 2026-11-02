package pe.test.exchange.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.test.exchange.domain.model.Conversion;
import pe.test.exchange.domain.repository.ConversionRepository;

@Repository
@RequiredArgsConstructor
public class ConversionRepositoryAdapter implements ConversionRepository {

    private final ConversionJpaRepository jpaRepository;


    @Override
    public Conversion save(Conversion conversion) {
        ConversionEntity saved = jpaRepository.save(toEntity(conversion));
        return toDomain(saved);
    }

    private ConversionEntity toEntity(Conversion conversion) {

        ConversionEntity entity = new ConversionEntity();
        entity.setId(conversion.getId());
        entity.setAmount(conversion.getAmount());
        entity.setSourceCurrency(conversion.getSourceCurrency());
        entity.setTargetCurrency(conversion.getTargetCurrency());
        entity.setExchangeRate(conversion.getExchangeRate());
        entity.setConvertedAmount(conversion.getConvertedAmount());
        entity.setCreatedAt(conversion.getCreatedAt());
        return entity;

    }

    private Conversion toDomain(ConversionEntity entity) {

        return new Conversion(
                entity.getId(),
                entity.getAmount(),
                entity.getSourceCurrency(),
                entity.getTargetCurrency(),
                entity.getExchangeRate(),
                entity.getConvertedAmount(),
                entity.getCreatedAt());
    }

}
