package osa.dev.petproject.models.db;

import lombok.Data;
import osa.dev.petproject.models.PreprocPointType;

import javax.persistence.*;

@Entity
@Data
@Table(name = "preproc_points", schema = "server_main")
public class PreprocPoint {

    @Id
    @SequenceGenerator(name = "preproc_points_id_seq",
                        sequenceName = "server_main.preproc_points_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "preproc_points_id_seq")
    @Column(name = "id", updatable = false)
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

    @Column(name = "dist_from_hb")
    private Double distFromHb;

}
