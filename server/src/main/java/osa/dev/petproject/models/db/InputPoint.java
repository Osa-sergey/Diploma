package osa.dev.petproject.models.db;

import lombok.Data;
import osa.dev.petproject.models.InputPointType;

import javax.persistence.*;

@Entity
@Data
@Table(name = "input_points", schema = "server_main")
public class InputPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "inp_point_type")
    private InputPointType type;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "pre_point")
    private Long prePoint;

    @Column(name = "post_point")
    private Long postPoint;

    @Column(name = "point_id")
    private Long pointId;
}
