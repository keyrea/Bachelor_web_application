package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT")
    private Long Id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "nationality", nullable = true)
    private String nationality;

    @Column(name = "speciality", nullable = true)
    private String speciality;

    @Column(name = "language", nullable = true)
    private String language;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "User2Hospital",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "hospital_id")
    )
    private List<Hospital> hospitals = new ArrayList<>();

}
