package osa.dev.petproject.models.db;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import osa.dev.petproject.models.OptimizationStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "optimizations", schema = "server_main")
public class Optimization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "title")
    private String title;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date date;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private OptimizationStatus status;

    @Column(name = "bs_number")
    private Integer bsNumber;

    @Column(name = "as_rad")
    private Float asRad;

    @Column(name = "bs_rad")
    private Float bsRad;

    @Column(name = "roadmap_id")
    private Integer roadmapId;
}
