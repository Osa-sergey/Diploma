package osa.dev.petproject.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.models.db.Optimization;
import osa.dev.petproject.repository.OptimizationRepository;

import java.util.List;

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
    public List<Optimization> getOptimizationsByUserId(@RequestParam(name = "user_id") Integer userId) {
        return optRepo.getAllOptByUserId(userId);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('opt:write')")
    public void deleteOptimizationById(@PathVariable Integer id) {
        optRepo.deleteById(id);
    }
}
