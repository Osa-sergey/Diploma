package osa.dev.petproject.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users", schema = "server_main")
public class AppUser {

    @Id
    @SequenceGenerator(name = "userSeq", sequenceName = "users_id_seq", schema = "server_main")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
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
