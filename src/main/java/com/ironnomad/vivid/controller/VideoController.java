package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.VideoDTO;
import com.ironnomad.vivid.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("")
@CrossOrigin
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/allVideos")
    public List<VideoDTO> getAllVideos(Model model) {
        return videoService.getAllVideos();
    }

//    @GetMapping("/videos/{videoId}")
//    public String getVideo(Model model, @PathVariable Long videoId) {
//        Video video = videoService.getVideoById(videoId);
//        model.addAttribute("video", video);
//        return "videoPage";
//    }

    @PostMapping("/upload")
    public String uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        videoService.uploadVideo(title, description, file);
        return "Video uploaded successfully!";
    }
}

