package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<User> createPatientProfile(@RequestBody User patient) {
        User createOrUpdatedPatient = patientService.createOrUpdatePatient(patient);
        return ResponseEntity.ok(createOrUpdatedPatient);
    }
}
