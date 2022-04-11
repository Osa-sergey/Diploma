package osa.dev.petproject.models.db;

import lombok.Data;
import osa.dev.petproject.models.OptimizationStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "optimization", schema = "server_main")
public class Optimization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer user_id;

    @Column(name = "title")
    private String title;

    @Temporal(TemporalType.DATE)
    @Column(name = "create_date")
    private Date date;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private OptimizationStatus status;

    @Column(name = "bs_number")
    private Integer bs_number;
}
