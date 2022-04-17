package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "roadmap_point_coords", schema = "server_main")
public class RoadmapPointCoord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_id")
    private Long pointId;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "roadmap_id")
    private Integer roadmapId;
}
