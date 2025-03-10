package com.ironnomad.vivid.repository;

import com.ironnomad.vivid.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserPageDTO {
    private String username;
    private LocalDate registrationDate;
    private Integer avatarCode;
    private List<Video> videos;
}