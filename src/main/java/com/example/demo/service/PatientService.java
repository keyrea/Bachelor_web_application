package com.example.demo.service;

import com.example.demo.exception.*;
import com.example.demo.model.Appointment;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PhysicianService physicianService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createPatient(User patient) {

        if(patient == null){
            throw new PatientNullException("Patient not provided");
        }

        if (patient.getName() == null || patient.getSurname() == null
                || patient.getDateOfBirth() == null || patient.getNationality() == null
                || patient.getCity() == null || patient.getPostalCode() == null
                || patient.getStreet() == null
                || patient.getEmail() == null || patient.getTelephone() == null
                || patient.getUsername() == null || patient.getRole() == null
                || patient.getPassword() == null) {

            throw new MissingRequiredFieldsException("Required fields for creation profile " +
            "are not provided");

        }

        // check if a patient with similar username exist
        if (userRepository.existsByUsername(patient.getUsername())) {
            throw new DuplicateUserException("User with the same username already exists");
        }

        String encodedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encodedPassword);

        return userRepository.save(patient);
    }

    public User updatePatient(User patient) {

        if (patient.getName() == null || patient.getSurname() == null
                || patient.getDateOfBirth() == null || patient.getNationality() == null
                || patient.getCity() == null || patient.getPostalCode() == null
                || patient.getStreet() == null
                || patient.getEmail() == null || patient.getTelephone() == null
                || patient.getUsername() == null || patient.getRole() == null
                || patient.getPassword() == null) {

            throw new MissingRequiredFieldsException("Required fields for creation profile " +
                    "are not provided.");

        }

        String encodedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encodedPassword);

        return userRepository.save(patient);

    }

    public void deletePatient(Long patientId) {

        User patient = userRepository.findById(patientId)
                        .orElseThrow(()->new PatientNotFoundException("Provided patient not found"));

        userRepository.delete(patient);

    }

    public Appointment reserveAppointment(Long patientId, Long appointmentId, Services service) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));

        User patient = userRepository.findById(patientId)
                .orElseThrow(()->new PatientNotFoundException("Patient not found"));

        // check whether chosen appointment is free
        if (appointmentService.checkAppointmentIsOccupied(appointment.getPatient())) {
            throw new AppointmentNotAvailableException("Appointment is not available for chosen date");
        }

        appointment.setPatient(patient);
        appointment.setService(service);

        // check for filling necessary data
        if (appointment.getService() == null) {
            throw new MissingRequiredFieldsException("Required fields are not provided");
        }

        // check if the chosen service belongs to the physician
        if (!physicianService.checkServiceToPhysician(appointment.getPhysician().getId(),service.getId())){
            throw new ServiceNotFoundException("Chosen service is not available by this physician");
        }

        return appointmentRepository.save(appointment);

    }

    public Appointment cancelReservation(Long patientId, Long appointmentId) {

        // find the patient and appointment
        User patient = userRepository.findById(patientId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Appointment not found"));

        // check if appointment is reserved by patient
        if(!appointment.getPatient().getId().equals(patientId)){
            throw new IllegalArgumentException("Chosen Appointment is not reserved by chosen patient or patient is not provided");
        }

        // cancel the reservation by setting patient and service to null
        appointment.setPatient(null);
        appointment.setService(null);

        return appointmentRepository.save(appointment);
    }

}
