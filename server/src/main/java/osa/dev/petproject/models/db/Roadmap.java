package osa.dev.petproject.models.db;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "roadmaps", schema = "server_main")
public class Roadmap {

    @Id
    @SequenceGenerator(name = "roadmaps_id_seq",
                        sequenceName = "server_main.roadmaps_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "roadmaps_id_seq")
    @Column(name = "id", updatable = false)
    private Integer id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date date;
}
