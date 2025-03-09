package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.User;
import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.LoginRequest;
import com.ironnomad.vivid.repository.UserDTO;
import com.ironnomad.vivid.repository.UserPageDTO;
import com.ironnomad.vivid.repository.UserRepository;
import com.ironnomad.vivid.service.JwtService;
import com.ironnomad.vivid.service.VideoMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private VideoMetadataService videoMetadataService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) {
        // Check if the username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new User and save to DB
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(hashedPassword);
        newUser.setRegistrationDate(LocalDate.now());
        newUser.setAvatarCode(request.getAvatarCode());

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        // Check if user exists and password matches
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        User user = userOpt.get(); // Now we have the User object
        String token = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        String username = principal.getName(); // Get username from SecurityContext

        Optional<User> userOpt = userRepository.findById(username); // Fetch user from DB
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        List<Video> videos = videoMetadataService.getVideosByUsername(username);
        UserPageDTO userPageDTO = new UserPageDTO(user.getUsername(), user.getRegistrationDate(),user.getAvatarCode() ,videos);
        return ResponseEntity.ok(userPageDTO);
    }
}
