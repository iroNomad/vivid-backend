package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.User;
import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.UserRepository;
import com.ironnomad.vivid.repository.VideoDTO;
import com.ironnomad.vivid.service.S3Service;
import com.ironnomad.vivid.service.VideoMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/video")
@CrossOrigin
public class VideoController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoMetadataService videoMetadataService;

    @GetMapping("/all")
    public List<VideoDTO> getAllVideos() {
        return videoMetadataService.getAllVideos();
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("video") MultipartFile videoFile,
            Principal principal // Automatically get authenticated user
    ) {
        String username = principal.getName(); // Get username from SecurityContext

        Optional<User> userOpt = userRepository.findById(username); // Fetch user from DB
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        try {
            // Upload video and generate thumbnail
            Map<String, String> fileUrls = s3Service.uploadVideo(username, title, description, videoFile);
            return ResponseEntity.ok(fileUrls);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{videoId}")
    public VideoDTO getVideoById(@PathVariable("videoId") Long videoId) {
        return videoMetadataService.getVideoById(videoId);
    }
}

