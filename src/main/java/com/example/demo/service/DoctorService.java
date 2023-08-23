package com.example.demo.service;

import com.example.demo.model.Appoitment;
import com.example.demo.repository.AppoitmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Service
public class DoctorService {

    @Autowired
    private AppoitmentRepository appoitmentRepository;

    public Appoitment updateReservationByDoctor(Long appoitmentId, LocalDate newDate, LocalTime newTime){
        Appoitment appoitment = appoitmentRepository.findById(appoitmentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        // update date and time of appointment
        appoitment.setDate(newDate);
        appoitment.setTime(newTime);

        return appoitmentRepository.save(appoitment);
    }

    public void deleteAppoitment(Long appoitmentId) {

        // fetch the appoitment from Repository
        Appoitment appoitment = appoitmentRepository.findById(appoitmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appoitment not found"));

        // delete the appointment
        appoitmentRepository.delete(appoitment);
    }
}
