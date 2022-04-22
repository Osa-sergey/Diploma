package osa.dev.petproject.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import osa.dev.petproject.dto.in.NewPostRequestDTO;
import osa.dev.petproject.exceptions.CheckException;
import osa.dev.petproject.models.OptimizationStatus;
import osa.dev.petproject.models.PreprocPointType;
import osa.dev.petproject.models.db.Optimization;
import osa.dev.petproject.models.db.PreprocPoint;
import osa.dev.petproject.repository.OptimizationRepository;
import osa.dev.petproject.repository.PreprocRepository;
import osa.dev.petproject.services.PostCheckOptimizationService;
import osa.dev.petproject.services.PreprocOptimizationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/optimizations")
public class OptimizationListRestControllerV1 {

    private final PreprocRepository preprocRepository;
    private final OptimizationRepository optRepo;
    private final PostCheckOptimizationService checkService;
    private final PreprocOptimizationService preprocService;

    @Autowired
    public OptimizationListRestControllerV1(PreprocRepository preprocRepository,
                                            OptimizationRepository optRepo,
                                            PostCheckOptimizationService checkService,
                                            PreprocOptimizationService preprocService){
        this.preprocRepository = preprocRepository;
        this.optRepo = optRepo;
        this.checkService = checkService;
        this.preprocService = preprocService;
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

//        ProcessBuilder pb = new ProcessBuilder("D:\\Диплом\\код\\diploma-server\\server\\venv\\Scripts\\python.exe",
//                                                "D:\\Диплом\\код\\diploma-server\\server\\src\\main\\python\\optimization.py",
//                                                "15");
//        try {
//            Process p = pb.start();
//            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while ((line = bfr.readLine()) != null) {
//                System.out.println("Python Output: " + line);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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
            preprocService.preproc(opt);
            printPreprocPoints(dto.getRoadmapId());
            optRepo.save(opt);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Incorrect data", HttpStatus.BAD_REQUEST);
        }
    }

    private void printPreprocPoints(Integer roadmapId) {
        for (PreprocPoint point: preprocRepository.findAllByRoadmapID(roadmapId)) {
            if(point.getType() == PreprocPointType.INTEREST_POINT) {
                System.out.println("<node id='" + point.getPointId() + "' action='modify' version='1'" +
                        " visible='true' lat='" + point.getLat() + "' lon='" + point.getLon() + "'>\n" +
                        "    <tag k='"+ point.getType() + "' v='true' />\n" +
                        "  </node>");
            } else {
                System.out.println("<node id='" + point.getPointId() + "' action='modify' version='1'" +
                        " visible='true' lat='" + point.getLat() + "' lon='" + point.getLon() + "' />");
            }
        }
    }
}
