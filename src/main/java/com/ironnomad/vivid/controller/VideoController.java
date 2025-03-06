package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.User;
import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.UserRepository;
import com.ironnomad.vivid.repository.VideoDTO;
import com.ironnomad.vivid.repository.VideoUpdateRequest;
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

    @PutMapping("/update/{videoId}")
    public ResponseEntity<Void> updateVideo(
            @PathVariable("videoId") Long videoId,
            @RequestBody VideoUpdateRequest request,
            Principal principal) {
        Optional<User> userOpt = getAuthenticatedUser(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        videoMetadataService.updateVideo(videoId, request.getTitle(), request.getDescription());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable("videoId") Long videoId, Principal principal) {
        Optional<User> userOpt = getAuthenticatedUser(principal);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        // Get video metadata to check ownership and get file paths
        VideoDTO video = videoMetadataService.getVideoById(videoId);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Video not found"));
        }

        // Check if the user owns the video
        if (!video.getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Not authorized to delete this video"));
        }

        try {
            // Delete from S3
            s3Service.deleteVideo(video.getVideoFileURL(), video.getThumbnailFileURL());
            // Delete metadata from DB
            videoMetadataService.deleteVideo(videoId);

            return ResponseEntity.ok(Map.of("message", "Video deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete video: " + e.getMessage()));
        }
    }


    public Optional<User> getAuthenticatedUser(Principal principal) {
        String username = principal.getName(); // Get username from SecurityContext
        return userRepository.findById(username); // Fetch user from DB
    }
}

