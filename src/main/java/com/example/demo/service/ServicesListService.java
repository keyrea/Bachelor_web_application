package com.example.demo.service;

import com.example.demo.model.Services;
import com.example.demo.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicesListService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Services findById(Long id) {
        Optional<Services> service = serviceRepository.findById(id);
        return service.orElse(null);
    }

}
