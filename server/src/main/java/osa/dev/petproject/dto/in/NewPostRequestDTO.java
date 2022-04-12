package osa.dev.petproject.dto.in;

import lombok.Data;

@Data
public class NewPostRequestDTO {
    private Integer roadmapId;
    private String title;
    private Integer bsNumber;
    private Float bsRad;
    private Float asRad;
    private Integer userId;
}
