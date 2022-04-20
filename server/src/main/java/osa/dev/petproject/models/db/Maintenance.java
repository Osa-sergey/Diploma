package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "maintenance", schema = "server_main")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interest_point_id")
    private Long interestPointId;

    @Column(name = "pos_point_id")
    private Long posPointId;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "is_maintenance")
    private Integer isMaintenance;
}
