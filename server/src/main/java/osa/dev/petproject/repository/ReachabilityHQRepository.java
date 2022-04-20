package osa.dev.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osa.dev.petproject.models.db.ReachabilityHQ;

public interface ReachabilityHQRepository extends JpaRepository<ReachabilityHQ, Long> {
}
