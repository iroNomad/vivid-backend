package com.ironnomad.vivid.controller;

import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("")
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
    public String uploadVideo(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description) throws IOException {
        videoService.uploadVideo(file, title, description);
        return "redirect:/";
    }
}

