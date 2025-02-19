package com.ironnomad.vivid.repository;

import com.ironnomad.vivid.entity.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, Integer> {
    Video findById(Long id);
}
