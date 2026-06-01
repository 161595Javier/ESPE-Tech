package ec.edu.espe.tech.repository;

import ec.edu.espe.tech.entity.HardwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HardwareRepository extends JpaRepository<HardwareEntity, Long> {
}
