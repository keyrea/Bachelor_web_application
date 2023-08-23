package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
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

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToMany
    @JoinTable( // specify Join table that manages relationships
            name = "User2Services", // name of Join table in database
            joinColumns = @JoinColumn(name = "user_id"), // column mapping for User entity
            inverseJoinColumns = @JoinColumn(name = "service_id") //column mapping for Services entity
    )
    private List<Services> services = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    private List<Appoitment> managedAppoitments = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    private List<Appoitment> appoitments = new ArrayList<>();

    // TODO: create "picture" attribute
    // TODO: create "city" attribute
    // TODO: create "education" attribute
    // TODO: create "experience" attribute

}
