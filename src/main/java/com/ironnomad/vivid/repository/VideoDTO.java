package com.ironnomad.vivid.repository;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class VideoDTO {
    private Long videoId;
    private String username;
    private String thumbnailFileURL;
    private String videoFileURL;
    private String title;
    private String description;
    private LocalDate uploadDate;

    public VideoDTO(Long videoId, String username, String thumbnailFileURL, String title,String description, LocalDate uploadDate) {
        this.videoId = videoId;
        this.username = username;
        this.thumbnailFileURL = thumbnailFileURL;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadDate;
    }

    public VideoDTO(String username, String videoFileURL, String thumbnailFileURL, String title, String description, LocalDate uploadDate) {
        this.username = username;
        this.videoFileURL = videoFileURL;
        this.thumbnailFileURL = thumbnailFileURL;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadDate;
    }
}

