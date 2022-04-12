package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.PreprocPoint;

public interface PreprocRepository extends JpaRepository<PreprocPoint, Long> {
}
