package osa.dev.petproject.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.dto.in.NewPostRequestDTO;
import osa.dev.petproject.exceptions.CheckException;
import osa.dev.petproject.models.OptimizationStatus;
import osa.dev.petproject.models.db.Optimization;
import osa.dev.petproject.repository.OptimizationRepository;
import osa.dev.petproject.services.PostCheckOptimizationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/optimizations")
public class OptimizationListRestControllerV1 {

    private final OptimizationRepository optRepo;
    private final PostCheckOptimizationService checkService;
    @Autowired
    public OptimizationListRestControllerV1(OptimizationRepository optRepo,
                                            PostCheckOptimizationService checkService){
        this.optRepo = optRepo;
        this.checkService = checkService;
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
        try {
            checkService.checkOptimizationPostContent(dto);
        } catch (CheckException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
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
