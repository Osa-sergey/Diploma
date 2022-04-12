package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.Roadmap;

public interface RoadmapRepository extends JpaRepository<Roadmap, Integer> {

}
