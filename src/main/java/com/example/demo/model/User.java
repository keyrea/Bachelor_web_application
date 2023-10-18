package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "postal_code", nullable = true)
    private String postalCode;

    @Column(name = "street", nullable = true)
    private String street;

    @Column(name = "speciality", nullable = true)
    private String speciality;

    @Column(name = "language", nullable = true)
    private String language;

    @Column(name = "education", nullable = true)
    private String education;

    @Column(name = "experience", nullable = true)
    private String experience;

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
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToMany
    @JoinTable( // specify Join table that manages relationships
            name = "User2Services", // name of Join table in database
            joinColumns = @JoinColumn(name = "user_id"), // column mapping for User entity
            inverseJoinColumns = @JoinColumn(name = "service_id") //column mapping for Services entity
    )
    private List<Services> services = new ArrayList<>();

    @OneToMany(mappedBy = "physician")
    private List<Appointment> managedAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<Appointment> appointments = new ArrayList<>();

}
