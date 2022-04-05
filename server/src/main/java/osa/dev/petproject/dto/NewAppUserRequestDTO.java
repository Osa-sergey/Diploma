package osa.dev.petproject.dto;

import lombok.Data;

@Data
public class NewAppUserRequestDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
