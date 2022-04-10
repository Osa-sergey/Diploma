package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import osa.dev.petproject.models.db.Optimization;

import java.util.List;

public interface OptimizationRepository extends JpaRepository<Optimization, Integer> {

    @Query(value = "select o" +
            " from Optimization as o, IN(o.user) as u" +
            " where u.id = :user_id order by o.date desc")
    public List<Optimization> getAllOptByUserId(@Param("user_id") Integer user_id);
}
