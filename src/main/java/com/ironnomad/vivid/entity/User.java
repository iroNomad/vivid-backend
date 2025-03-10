package com.ironnomad.vivid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @Column(length = 50) // Limit username length
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false) // Registration date should not change
    private LocalDate registrationDate;

    @Column(nullable = false)
    private int avatarCode;
}
