package com.example.demo.controller;

import com.example.demo.exception.DuplicateUserException;
import com.example.demo.exception.MissingRequiredFieldsException;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.User;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup/patient")
public class PatientSignUpController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<User>> createPatient(@RequestBody User patient) {

        try {

            User createdPatient = patientService.createPatient(patient);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Patient profile is created", createdPatient));

        }

        catch (MissingRequiredFieldsException | DuplicateUserException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create patient profile: "+e.getMessage()));

        }

    }
}
