package com.example.demo.service;

import com.example.demo.model.Appoitment;
import com.example.demo.model.User;
import com.example.demo.repository.AppoitmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppoitmentRepository appoitmentRepository;

    public User createDoctor(User doctor) {
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

    public Appoitment createOrUpdateAppoitment(Appoitment appoitment){

        // Ensure doctorId is provided
        Long doctorId = appoitment.getDoctor().getId();
        if(doctorId == null) {
            throw new IllegalArgumentException("Doctor Id is required");
        }

        // set other attributes and values
        appoitment.setPatient(null);
        appoitment.setService(null);

        return appoitmentRepository.save(appoitment);
    }

}
