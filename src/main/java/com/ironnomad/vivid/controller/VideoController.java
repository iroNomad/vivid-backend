package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.User;
import com.ironnomad.vivid.repository.UserRepository;
import com.ironnomad.vivid.repository.VideoDTO;
import com.ironnomad.vivid.repository.LoginRequest;
import com.ironnomad.vivid.service.JwtService;
import com.ironnomad.vivid.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@CrossOrigin
public class VideoController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) {
        // Check if the username already exists
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new User and save to DB
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(hashedPassword);
        newUser.setRegistrationDate(LocalDate.now());

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // ✅ Generate JWT token (using Java JWT library)
        String token = jwtService.generateToken(user);

        // ✅ Send token to frontend
        return ResponseEntity.ok(Map.of("token", token));
    }


    @GetMapping("/allVideos")
    public List<VideoDTO> getAllVideos() {
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

