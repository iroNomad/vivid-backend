package com.ironnomad.vivid.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Data
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Works with MySQL AUTO_INCREMENT
    private Long videoId;

    @Column(nullable = false)
    private Long userId = 100L;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 255, nullable = false)
    private String thumbnailFileURL;

    @Column(length = 255, nullable = false)
    private String videoFileURL;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(columnDefinition = "DATE") // ✅ Ensures MySQL stores only DATE (not DATETIME)
    private LocalDate uploadDate;
}
