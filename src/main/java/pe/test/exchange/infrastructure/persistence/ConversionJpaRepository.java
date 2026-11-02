package pe.test.exchange.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionJpaRepository extends JpaRepository<ConversionEntity , Long> {
}
