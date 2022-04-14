package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.InputPointType;
import osa.dev.petproject.models.db.InputPoint;

import java.util.List;

public interface InputPointRepository extends JpaRepository<InputPoint, Long> {

    List<InputPoint> findByRoadmapIdAndType(Integer roadmapId, InputPointType type);
}
