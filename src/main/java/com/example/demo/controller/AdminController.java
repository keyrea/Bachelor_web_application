package com.example.demo.controller;

import com.example.demo.model.Appoitment;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/doctors")
    public ResponseEntity<User> createDoctor(@RequestBody User doctor) {
        logger.info("Received request to create a new doctor: {}", doctor);

        User createdDoctor = adminService.createDoctor(doctor);

        logger.info("Doctor created successfully: {}", createdDoctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    @PutMapping("/doctors/{id}")
    public User updateDoctor(@PathVariable Long id, @RequestBody User doctor) {
        doctor.setId(id); // Set the id for Update
        return adminService.updateDoctor(doctor);
    }

    @DeleteMapping("/doctors/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        adminService.deleteDoctor(id);
    }

    @PostMapping("/create/appointments")
    public ResponseEntity<Appoitment> createOrUpdateAppoitment(@RequestBody Appoitment appoitment) {
        Appoitment savedAppoitment = adminService.createOrUpdateAppoitment(appoitment);
        return ResponseEntity.ok(savedAppoitment);
    }

    // TODO: implement GET method for list of doctors

}
