package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.repository.VideoDTO;
import com.ironnomad.vivid.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@CrossOrigin
public class VideoController {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/allVideos")
    public List<VideoDTO> getAllVideos(Model model) {
        return s3Service.getAllVideos();
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("video") MultipartFile videoFile
    ) {
        try {
            // Upload video and generate thumbnail
            Map<String, String> fileUrls = s3Service.uploadVideo(title, description, videoFile);

            return ResponseEntity.ok(fileUrls);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/video/{videoId}")
    public VideoDTO getVideoById(@PathVariable("videoId") Long videoId) {
        return s3Service.getVideoById(videoId);
    }
}

