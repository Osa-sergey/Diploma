package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "roadmap_points", schema = "server_main")
public class RoadmapPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
