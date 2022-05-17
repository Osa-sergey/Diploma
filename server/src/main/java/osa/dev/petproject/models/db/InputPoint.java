package osa.dev.petproject.models.db;

import lombok.Data;
import osa.dev.petproject.models.InputPointType;

import javax.persistence.*;

@Entity
@Data
@Table(name = "input_points", schema = "server_main")
public class InputPoint {

    @Id
    @SequenceGenerator(name = "input_points_id_seq",
                        sequenceName = "server_main.input_points_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "input_points_id_seq")
    @Column(name = "id", updatable = false)
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
