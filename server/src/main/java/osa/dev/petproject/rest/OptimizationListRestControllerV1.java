package osa.dev.petproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.dto.out.OptimizationDTO;
import osa.dev.petproject.repository.OptimizationRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/optimizations")
public class OptimizationListRestControllerV1 {

    private final OptimizationRepository optRepo;

    @Autowired
    public OptimizationListRestControllerV1(OptimizationRepository optRepo){
        this.optRepo = optRepo;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('opt:read')")
    public List<OptimizationDTO> getOptimizationsById(@RequestParam(name = "user_id") Integer userId) {
        return optRepo.getAllOptByUserId(userId).stream()
                .map(o -> new OptimizationDTO(o.getId(), o.getTitle(), o.getDate(), o.getStatus().name(),o.getBs_number()))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('opt:write')")
    public void deleteOptimizationById(@PathVariable Integer id) {
        optRepo.deleteById(id);
    }
}
