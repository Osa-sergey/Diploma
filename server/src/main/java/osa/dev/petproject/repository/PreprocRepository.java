package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.PreprocPoint;

import java.util.ArrayList;

public interface PreprocRepository extends JpaRepository<PreprocPoint, Long> {

    @Query("SELECT COUNT(p) FROM PreprocPoint p WHERE p.roadmapID=?1")
    long countByRoadmapID(Integer roadmapId);

    ArrayList<PreprocPoint> findAllByRoadmapID(Integer roadmapId);

    @Query("SELECT p FROM PreprocPoint p WHERE p.distFromHb<=?1 AND p.roadmapID=?2 AND p.type =?3")
    ArrayList<PreprocPoint> getPointsCloserThan(Double dist, Integer roadmapId, PreprocPointType type);

    ArrayList<PreprocPoint> getPreprocPointByTypeAndRoadmapID(PreprocPointType type, Integer roadmapId);
}
