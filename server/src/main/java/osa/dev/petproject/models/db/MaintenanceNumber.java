package osa.dev.petproject.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "maintenance_number", schema = "server_main")
public class MaintenanceNumber {

    @Id
    @SequenceGenerator(name = "maintenance_number_id_seq",
                        sequenceName = "server_main.maintenance_number_id_seq",
                        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "maintenance_number_id_seq")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "pos_point_id")
    private Long posPointId;

    @Column(name = "roadmap_id")
    private Integer roadmapId;

    @Column(name = "number")
    private Integer number;
}
