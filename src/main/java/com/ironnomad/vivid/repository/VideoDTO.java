package com.ironnomad.vivid.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class VideoDTO {
    private Long userId;
    private String title;
    private LocalDateTime uploadDateTime;
}

