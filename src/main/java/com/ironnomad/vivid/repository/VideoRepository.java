package com.ironnomad.vivid.repository;

import com.ironnomad.vivid.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    Video findById(Long id);
}
