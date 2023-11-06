package com.example.demo.controller;

import com.example.demo.dtos.ReserveAppointmentRequest;
import com.example.demo.exception.AppointmentNotAvailableException;
import com.example.demo.exception.MissingRequiredFieldsException;
import com.example.demo.exception.ServiceNotFoundException;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Appointment;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.DuplicateFormatFlagsException;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ServiceRepository serviceRepository;

    private final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @PutMapping("/profile/update/{patientId}")
    public ResponseEntity<ApiResponse<User>> updatePatient(@PathVariable Long patientId,
                                                           @RequestBody User patient) {

        try {

            patient.setId(patientId); // Set the id for Update
            User updatedPatient = patientService.updatePatient(patient);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                    .success("Patient profile is updated successfully",updatedPatient));

        }

        catch (MissingRequiredFieldsException | DuplicateFormatFlagsException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update patient profile: "+e.getMessage()));

        }

    }

    @DeleteMapping("/profile/delete/{patientId}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long patientId) {

        patientService.deletePatient(patientId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Patients's profile is deleted successfully", null));

    }

    @PostMapping("/{patientId}/appointments/reserve/{appointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> reserveAppointment(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId,
            @RequestBody ReserveAppointmentRequest requestBody) {

        logger.info("Received request to reserve an appointment: {}", appointmentId);

        try {

            Services service = requestBody.getService();

            Services chosenService = serviceRepository.findById(service.getId())
                    .orElseThrow(()->new RuntimeException("Service not found"));

            Appointment reservedAppointment = patientService.reserveAppointment(patientId, appointmentId, chosenService);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                    .success("Appointment is reserved successfully", reservedAppointment));

        }

        catch (MissingRequiredFieldsException | AppointmentNotAvailableException
               | ServiceNotFoundException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to reserve appointment: "+e.getMessage()));

        }

    }

    @PutMapping("/{patientId}/appointments/update/{oldAppointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> updateReservationByPatient(
            @PathVariable Long patientId,
            @PathVariable Long oldAppointmentId,
            @RequestBody Map<String,String> requestBody) {

        try {

            String newAppointmentIdString = requestBody.get("newAppointmentId");

            Long newAppointmentId = Long.parseLong(newAppointmentIdString);

            Appointment updatedAppointment = appointmentService.updateReservation(patientId,
                    oldAppointmentId,newAppointmentId);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                    .success("Appointment is updated successfully",updatedAppointment));

        }

        catch (AppointmentNotAvailableException | MissingRequiredFieldsException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update reserve: "+e.getMessage()));

        }
    }

    @PostMapping("/{patientId}/appointments/cancel/{appointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> cancelReservation(
            @PathVariable Long patientId,
            @PathVariable Long appointmentId) {

        Appointment canceledAppointment = patientService.cancelReservation(patientId,appointmentId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .success("Appointment is canceled", canceledAppointment));

    }

}