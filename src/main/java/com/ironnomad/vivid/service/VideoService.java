package com.ironnomad.vivid.service;

import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private final String uploadDir = "videoAssets/"; // Folder inside the Spring Boot root directory

    public void uploadVideo(String title, String description, MultipartFile file) throws IOException {
        // Ensure videoAssets directory exists in the project root
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs(); // Create the directory if missing
        }

        // Generate file path inside videoAssets/
        String filePath = uploadDir + file.getOriginalFilename();
        Path path = Paths.get(filePath);

        // Save the file to videoAssets folder
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to the database
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setFilePath(filePath); // Store file path in DB

        videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return getAllVideos();
    }

    public Video getVideoById(Long videoId) {
        return getVideoById(videoId);
    }
}
