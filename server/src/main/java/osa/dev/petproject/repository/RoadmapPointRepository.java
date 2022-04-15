package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import osa.dev.petproject.models.db.RoadmapPoint;

import java.util.ArrayList;

public interface RoadmapPointRepository extends JpaRepository<RoadmapPoint, Long> {

    RoadmapPoint findRoadmapPointByPointIdAndNeibPointIdAndRoadmapId(Long pointId, Long neibPointId, Integer roadmapId);

    @Query(value = "SELECT DISTINCT r.pointId FROM RoadmapPoint r WHERE r.roadmapId = ?1")
    ArrayList<Long> getDistinctPointIds(Integer roadmapId);

    ArrayList<RoadmapPoint> findRoadmapPointByPointIdAndRoadmapId(Long pointId, Integer roadmapId);
}
