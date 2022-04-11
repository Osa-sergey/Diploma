package osa.dev.petproject.models.db;

import lombok.Data;
import osa.dev.petproject.models.Role;
import osa.dev.petproject.models.Status;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users", schema = "server_main")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
