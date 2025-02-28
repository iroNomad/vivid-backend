package com.ironnomad.vivid.repository;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class VideoDTO {
    private Long videoId;
    private Long userId;
    private String thumbnailFileURL;
    private String videoFileURL;
    private String title;
    private String description;
    private LocalDate uploadDate;

    public VideoDTO(Long videoId, Long userId, String thumbnailFileURL, String title, LocalDate uploadDate) {
        this.videoId = videoId;
        this.userId = userId;
        this.thumbnailFileURL = thumbnailFileURL;
        this.title = title;
        this.uploadDate = uploadDate;
    }

    public VideoDTO(Long userId, String videoFileURL, String title, String description, LocalDate uploadDate) {
        this.userId = userId;
        this.videoFileURL = videoFileURL;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadDate;
    }
}

