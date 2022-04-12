package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.InputPoint;

public interface InputPointRepository extends JpaRepository<InputPoint, Long> {
}
