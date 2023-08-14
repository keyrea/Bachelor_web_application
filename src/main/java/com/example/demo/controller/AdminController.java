package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getAllDoctors() {
        List<User> doctors = adminService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @PostMapping("/doctors")
    public User createDoctor(@RequestBody User doctor) {
        return adminService.createDoctor(doctor);
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

}
