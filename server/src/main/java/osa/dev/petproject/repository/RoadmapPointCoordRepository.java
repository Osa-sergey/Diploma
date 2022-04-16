package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.RoadmapPointCoord;

import java.util.ArrayList;

public interface RoadmapPointCoordRepository extends JpaRepository<RoadmapPointCoord, Long> {

    RoadmapPointCoord findRoadmapPointCoordByPointIdAndRoadmapId(Long pointId, Integer roadmapId);

    ArrayList<RoadmapPointCoord> findRoadmapPointCoordByRoadmapId(Integer roadmapId);
}
