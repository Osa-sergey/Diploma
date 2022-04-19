package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "reachability_hq", schema = "server_main")
public class ReachabilityHQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_point_id")
    private Long posPointId;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "is_reachable")
    private Integer isReachable;
}
