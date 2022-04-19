package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.Maintenance;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

}
