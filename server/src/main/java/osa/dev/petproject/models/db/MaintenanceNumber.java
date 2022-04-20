package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "maintenance_number", schema = "server_main")
public class MaintenanceNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_point_id")
    private Long posPointId;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "number")
    private Integer number;
}
