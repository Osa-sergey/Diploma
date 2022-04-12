package osa.dev.petproject.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    @PostMapping
    @RequestMapping(headers = ("content-type=multipart/*"), consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('opt:write')")
    public List<String> handleFileUpload(@RequestParam(name = "file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<String> input = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                                .lines()
                                .collect(Collectors.toList());
        return input;
    }
}
