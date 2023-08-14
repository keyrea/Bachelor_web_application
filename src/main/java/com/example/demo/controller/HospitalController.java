package com.example.demo.controller;

import com.example.demo.model.Hospital;
import com.example.demo.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {

    @Autowired
    private HospitalRepository hospitalRepository;

    @GetMapping("/list")
    public List<Hospital> getAllHospitals(){
        return hospitalRepository.findAll();
    }
}
