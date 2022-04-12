package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.RoadmapPoint;

public interface RoadmapPointRepository extends JpaRepository<RoadmapPoint, Long> {
}
