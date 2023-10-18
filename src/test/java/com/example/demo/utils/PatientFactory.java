package com.example.demo.utils;

import com.example.demo.model.User;

import java.time.LocalDate;

public class PatientFactory {

    public static User createPatient() {

        User patient = new User();

        patient.setName("John");
        patient.setSurname("Doe");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setNationality("DE");
        patient.setCity("Schmalkalden");
        patient.setPostalCode("98574");
        patient.setStreet("Blechhammer 9b");
        patient.setEmail("john@example.com");
        patient.setTelephone("+123456789");
        patient.setUsername("john_doe");
        patient.setRole("PATIENT");
        patient.setPassword("my_secret_password");

        return patient;

    }

}
