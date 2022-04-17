package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import osa.dev.petproject.models.db.PreprocPoint;

public interface PreprocRepository extends JpaRepository<PreprocPoint, Long> {

    @Query("SELECT COUNT(p) FROM PreprocPoint p WHERE p.roadmapID=?1")
    long countByRoadmapID(Integer roadmapId);
}
