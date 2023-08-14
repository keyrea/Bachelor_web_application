package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public User createDoctor(User doctor) {
        doctor.setRole("DOCTOR");
        return userRepository.save(doctor);
    }

    public User updateDoctor(User doctor) {
        return userRepository.save(doctor);
    }

    public void deleteDoctor(Long doctorId) {
        userRepository.deleteById(doctorId);
    }

    public List<User> getAllDoctors(){
        return userRepository.findByRole("DOCTOR");
    }

}
