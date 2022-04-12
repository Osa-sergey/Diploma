package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import osa.dev.petproject.models.db.Optimization;

import java.util.List;
import java.util.Optional;

public interface OptimizationRepository extends JpaRepository<Optimization, Integer> {

    @Query(value = "select o" +
            " from Optimization as o" +
            " where o.userId = :user_id order by o.date desc")
    List<Optimization> getAllOptByUserId(@Param("user_id") Integer user_id);

    Optional<Optimization> findByRoadmapId(Integer id);
}
