package com.ironnomad.vivid.repository;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
    private Integer avatarCode;
}
