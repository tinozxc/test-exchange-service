package pe.test.exchange.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.test.exchange.domain.model.Conversion;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConversionRepositoryAdapterTest {

    @Mock
    private ConversionJpaRepository jpaRepository;

    @InjectMocks
    private ConversionRepositoryAdapter adapter;

    @Test
    void mapsDomainToEntityAndBack() {
        Conversion conversion = new Conversion(
                new BigDecimal("100"), "USD", "PEN", new BigDecimal("3.7051"));

        when(jpaRepository.save(any(ConversionEntity.class))).thenAnswer(invocation -> {
            ConversionEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        Conversion saved = adapter.save(conversion);

        ArgumentCaptor<ConversionEntity> captor = ArgumentCaptor.forClass(ConversionEntity.class);
        verify(jpaRepository).save(captor.capture());
        ConversionEntity persisted = captor.getValue();
        assertThat(persisted.getSourceCurrency()).isEqualTo("USD");
        assertThat(persisted.getTargetCurrency()).isEqualTo("PEN");
        assertThat(persisted.getConvertedAmount()).isEqualByComparingTo("370.51");
        assertThat(persisted.getCreatedAt()).isEqualTo(conversion.getCreatedAt());

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getConvertedAmount()).isEqualByComparingTo("370.51");
        assertThat(saved.getCreatedAt()).isEqualTo(conversion.getCreatedAt());
    }

}
