package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "roadmap_points", schema = "server_main")
public class RoadmapPoint {

    @Id
    @SequenceGenerator(name = "roadmap_points_id_seq",
                        sequenceName = "server_main.roadmap_points_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "roadmap_points_id_seq")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "point_id")
    private Long pointId;

    @Column(name = "neib_id")
    private Long neibPointId;

    @Column(name = "dist")
    private Double dist;
}
