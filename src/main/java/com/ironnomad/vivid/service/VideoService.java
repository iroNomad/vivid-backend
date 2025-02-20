package com.ironnomad.vivid.service;

import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private final String uploadDir = "uploads/";

    public Video uploadVideo(MultipartFile file, String title, String description) throws IOException {

        String filename = title + "_" + file.getOriginalFilename();

        // Define the full path to save the file
        Path path = Paths.get(uploadDir, filename);
        Files.createDirectories(path.getParent()); // Ensure folder exists
        Files.write(path, file.getBytes()); // Save file

        // Save only relative path in DB
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setFilePath(filename);

        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id).get();
    }
}
