package osa.dev.petproject.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import osa.dev.petproject.models.db.Roadmap;
import osa.dev.petproject.repository.RoadmapRepository;
import osa.dev.petproject.services.JosmXmlOptimizationParserService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final RoadmapRepository roadmapRepo;
    private final JosmXmlOptimizationParserService parserService;

    @Autowired
    public FileUploadController(RoadmapRepository roadmapRepo,
                                JosmXmlOptimizationParserService parserService) {
        this.roadmapRepo = roadmapRepo;
        this.parserService = parserService;
    }

    @PostMapping
    @RequestMapping(headers = ("content-type=multipart/*"), consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('opt:write')")
    public Integer handleFileUpload(@RequestParam(name = "file") MultipartFile file) throws IOException {
        Roadmap roadmap = roadmapRepo.save(new Roadmap());
        InputStream inputStream = file.getInputStream();
        parserService.parse(inputStream, roadmap.getId());
        return roadmap.getId();
    }
}
