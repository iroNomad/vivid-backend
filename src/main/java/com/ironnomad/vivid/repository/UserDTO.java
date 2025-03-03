package com.ironnomad.vivid.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserDTO {
    private String username;
    private LocalDate registrationDate;
}