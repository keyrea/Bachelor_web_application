package com.example.demo.service;

import com.example.demo.model.Appoitment;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.AppoitmentRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Service
public class PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppoitmentRepository appoitmentRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createOrUpdatePatient(User patient) {
        String encodedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encodedPassword);
        return userRepository.save(patient);
    }

    public void deletePatient(Long id) {
        userRepository.deleteById(id);
    }

    public Appoitment reserveAppoitment(Long patientId, Long appoitmentId, Services service) {

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        Appoitment appoitment = appoitmentRepository.findById(appoitmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appoitment.getPatient() != null || appoitment.getService() != null) {
            throw new IllegalArgumentException("Appoitment is not available for reservation");
        }

        appoitment.setPatient(patient);
        appoitment.setService(service);

        return appoitmentRepository.save(appoitment);
    }

    public Appoitment updateReservationByPatient(Long patientId, Long appoitmentId, LocalDate newDate, LocalTime newTime) {

        Appoitment appoitment = appoitmentRepository.findById(appoitmentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Appointment not found"));

        // update date and time of appointment
        appoitment.setDate(newDate);
        appoitment.setTime(newTime);

        return appoitmentRepository.save(appoitment);
    }

    public Appoitment cancelReservation(Long patientId, Long appoitmentId) {

        // find the patient and appointment
        User patient = userRepository.findById(patientId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        Appoitment appoitment = appoitmentRepository.findById(appoitmentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Appointment not found"));

        // check if appointment is reserved by patient
        if(appoitment.getPatient() == null && !appoitment.getPatient().getId().equals(patientId)){
            throw new IllegalArgumentException("Invalid appointment");
        }

            // cancel the reservation by setting patient and service to null
            appoitment.setPatient(null);
            appoitment.setService(null);

            return appoitmentRepository.save(appoitment);
    }
}
