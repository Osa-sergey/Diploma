package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "reachability_hq", schema = "server_main")
public class ReachabilityHQ {

    @Id
    @SequenceGenerator(name = "reachability_hq_id_seq",
                        sequenceName = "server_main.reachability_hq_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "reachability_hq_id_seq")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "pos_point_id")
    private Long posPointId;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "is_reachable")
    private Integer isReachable;
}
