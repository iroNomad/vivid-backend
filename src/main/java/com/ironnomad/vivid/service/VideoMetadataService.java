package com.ironnomad.vivid.service;

import com.ironnomad.vivid.entity.User;
import com.ironnomad.vivid.entity.Video;
import com.ironnomad.vivid.repository.VideoDTO;
import com.ironnomad.vivid.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoMetadataService {

    @Autowired
    private VideoRepository videoRepository;

    public Map<String, String> saveVideoMetadata(User user, String title, String description, Map<String, String> fileUrls) {
        Video video = new Video();
        video.setUser(user);
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoFileURL(fileUrls.get("videoUrl"));
        video.setThumbnailFileURL(fileUrls.get("thumbnailUrl"));

        videoRepository.save(video);
        return fileUrls;
    }

    public List<VideoDTO> getAllVideos() {
        List<VideoDTO> videos = videoRepository.findAll()
                .stream()
                .map(video -> new VideoDTO(
                        video.getVideoId(),
                        video.getUser().getUsername(),
                        video.getThumbnailFileURL(),
                        video.getTitle(),
                        video.getDescription(),
                        video.getUploadDate()
                ))
                .collect(Collectors.toList());
        Collections.reverse(videos);
        return videos;
    }
    public VideoDTO getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .map(video -> new VideoDTO(
                        video.getUser().getUsername(),
                        video.getVideoFileURL(),
                        video.getTitle(),
                        video.getDescription(),
                        video.getUploadDate()
                ))
                .orElse(null);
    }
    public List<Video> getVideosByUsername(String username) {
        return videoRepository.findByUser_Username(username);
    }

    public void updateVideo(Long videoId, String title, String description) {
        videoRepository.findById(videoId).ifPresent(video -> {
            video.setTitle(title);
            video.setDescription(description);
            videoRepository.save(video);
        });
    }
}
