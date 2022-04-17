package osa.dev.petproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import osa.dev.petproject.dto.in.NewPostRequestDTO;
import osa.dev.petproject.exceptions.CheckException;
import osa.dev.petproject.repository.OptimizationRepository;

@Service
public class PostCheckOptimizationService {

    private final OptimizationRepository optRepo;

    @Autowired
    public PostCheckOptimizationService(OptimizationRepository optRepo){
        this.optRepo = optRepo;
    }

    public void checkOptimizationPostContent(NewPostRequestDTO dto) throws CheckException {
        if(optRepo.findByRoadmapId(dto.getRoadmapId()).isPresent()) {
            throw new CheckException("Optimization with this file id already exists", HttpStatus.BAD_REQUEST);
        }
        if(dto.getBsNumber() <= 0) {
            throw new CheckException("The number of BS must be a positive number", HttpStatus.BAD_REQUEST);
        }
        if(dto.getAsRad() <= 0 || dto.getBsRad() <= 0) {
            throw new CheckException("The radius of action must be positive", HttpStatus.BAD_REQUEST);
        }
        if(dto.getAsRad() > dto.getBsRad()) {
            throw new CheckException("The radius of subscribers should not be greater than the radius of the base station", HttpStatus.BAD_REQUEST);
        }
    }
}
