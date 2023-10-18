package com.example.demo.dtos;

import com.example.demo.model.Services;
import com.example.demo.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class ReserveAppointmentRequest {

    @Autowired
    private Services service;

    @Autowired
    private User patient;

}
