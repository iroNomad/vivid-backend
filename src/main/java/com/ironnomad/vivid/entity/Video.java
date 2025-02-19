package com.ironnomad.vivid.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "videos")
@Data
public class Video {
    @Id
    private Long id;
    private String title;
    private String description;
    private String filePath;
}
