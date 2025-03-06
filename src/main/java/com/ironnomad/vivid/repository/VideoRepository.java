package com.ironnomad.vivid.repository;

import com.ironnomad.vivid.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByUser_Username(String username);
    Optional<Video> findById(Long videoId);
}
