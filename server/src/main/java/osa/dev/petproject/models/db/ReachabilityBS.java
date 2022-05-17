package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "reachability_bs", schema = "server_main")
public class ReachabilityBS {

    @Id
    @SequenceGenerator(name = "reachability_bs_id_seq",
                        sequenceName = "server_main.reachability_bs_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "reachability_bs_id_seq")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "pos_point_id_1")
    private Long posPointId1;

    @Column(name = "pos_point_id_2")
    private Long posPointId2;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "is_reachable")
    private Integer isReachable;
}
