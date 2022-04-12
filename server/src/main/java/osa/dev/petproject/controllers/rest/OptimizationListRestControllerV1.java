package osa.dev.petproject.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.dto.in.NewPostRequestDTO;
import osa.dev.petproject.models.OptimizationStatus;
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

    @PostMapping
    @PreAuthorize("hasAuthority('opt:write')")
    public ResponseEntity<?> addOptimizationWithRoadmapId(@RequestBody NewPostRequestDTO dto){
        if(optRepo.findByRoadmapId(dto.getRoadmapId()).isPresent())
            return new ResponseEntity<>("Optimization with this file id already exists", HttpStatus.CONFLICT);
        try {
            Optimization opt = new Optimization();
            opt.setTitle(dto.getTitle());
            opt.setStatus(OptimizationStatus.IN_PROCESS);
            opt.setBsNumber(dto.getBsNumber());
            opt.setUserId(dto.getUserId());
            opt.setBsRad(dto.getBsRad());
            opt.setAsRad(dto.getAsRad());
            opt.setRoadmapId(dto.getRoadmapId());
            optRepo.save(opt);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Incorrect data", HttpStatus.BAD_REQUEST);
        }
    }
}
