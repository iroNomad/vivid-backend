package com.ironnomad.vivid.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "videoId", nullable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    @Column(length = 1000, nullable = false)
    private String content;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;
}