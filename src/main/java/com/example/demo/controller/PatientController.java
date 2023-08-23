package com.example.demo.controller;

import com.example.demo.model.Appoitment;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/profile")
    public ResponseEntity<User> createOrUpdatePatientProfile(@RequestBody User patient) {
        User createOrUpdatedPatient = patientService.createOrUpdatePatient(patient);
        return ResponseEntity.ok(createOrUpdatedPatient);
    }

    @DeleteMapping("/profile/{patientId}")
    public ResponseEntity<Void> deletePatientProfile(@PathVariable Long patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.noContent().build(); // return ResponseEntity with HTTP status 204
    }

    @PostMapping("/{patientId}/appoitments/{appoitmentId}/reserve")
    public ResponseEntity<Appoitment> reserveAppoitment(
            @PathVariable Long patientId,
            @PathVariable Long appoitmentId,
            @RequestBody Map<String, Services> requestBody) {

        Services service = requestBody.get("service");
        Appoitment reservedAppoitment = patientService.reserveAppoitment(patientId,appoitmentId,service);
        return ResponseEntity.ok(reservedAppoitment);

    }

    @PutMapping("/{patientId}/appoitments/{appoitmentId}")
    public ResponseEntity<Appoitment> updateReservationByPatient(
            @PathVariable Long patientId,
            @PathVariable Long appoitmentId,
            @RequestBody Map<String,String> requestBody
    ) {
        String newDateString = requestBody.get("newDate");
        String newTimeString = requestBody.get("newTime");

        if (newDateString == null || newTimeString == null) {
            return ResponseEntity.badRequest().build();
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate newDate = LocalDate.parse(newDateString, dateFormatter);
        LocalTime newTime = LocalTime.parse(newTimeString, timeFormatter);

        Appoitment updatedAppointment = patientService.updateReservationByPatient(patientId, appoitmentId, newDate, newTime);
        return ResponseEntity.ok(updatedAppointment);
    }

    @PostMapping("/{patientId}/appoitments/{appoitmentId}/cancel")
    public ResponseEntity<Appoitment> cancelReservation(
            @PathVariable Long patientId,
            @PathVariable Long appoitmentId) {

        Appoitment canceledAppoitment = patientService.cancelReservation(patientId,appoitmentId);
        return ResponseEntity.ok(canceledAppoitment);
    }

}