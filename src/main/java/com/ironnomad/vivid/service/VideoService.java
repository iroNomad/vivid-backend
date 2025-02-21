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

//    private final String uploadDir = "uploads/";

    public Video uploadVideo(Video video) throws IOException {
        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with ID: " + id));
    }
}
