package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("")
@CrossOrigin
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/")
    public String getAllVideos(Model model) {
        List<Video> videos = videoService.getAllVideos();
        model.addAttribute("videos", videos);
        return "index";
    }

    @GetMapping("/videos/{videoId}")
    public String getVideo(Model model, @PathVariable Long videoId) {
        Video video = videoService.getVideoById(videoId);
        model.addAttribute("video", video);
        return "videoPage";
    }

    @PostMapping("/upload")
    public String uploadVideo(@RequestBody Video video) throws IOException {
        videoService.uploadVideo(video);
        return "New video added";
    }
}

