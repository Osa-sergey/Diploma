package osa.dev.petproject.models.db;

import lombok.Data;
import osa.dev.petproject.models.PreprocPointType;

import javax.persistence.*;

@Entity
@Data
@Table(name = "preproc_points", schema = "server_main")
public class PreprocPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roadmap_id")
    private Integer roadmapID;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "preproc_point_type")
    private PreprocPointType type;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "point_id")
    private Long pointId;

}
