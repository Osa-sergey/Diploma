package osa.dev.petproject.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OptimizationDTO {

    private Integer id;
    private String title;
    private Date date;
    private String status;
    private Integer bs_number;
}
