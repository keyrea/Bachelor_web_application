package com.example.demo.service;

import com.example.demo.exception.PhysicianNotFoundException;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhysicianService {

    @Autowired
    private UserRepository userRepository;

    // there are help-methods for checking
    public boolean checkServiceToPhysician(Long physicianId, Long serviceId){

        User physician = userRepository.findById(physicianId)
                .orElseThrow(()->new PhysicianNotFoundException("Physician not found"));

        List<Services> physicianServices = physician.getServices();

        if(physicianServices == null) {
            return false;
        }

        return physicianServices.stream().anyMatch(service->service.getId().equals(serviceId));

    }

}
