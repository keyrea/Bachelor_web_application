package com.example.demo.controller;

import com.example.demo.model.Appoitment;
import com.example.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PutMapping("/{doctorId}/appointments/{appoitmentId}")
    public ResponseEntity<Appoitment> updateReservationByDoctor(
            @PathVariable Long doctorId,
            @PathVariable Long appoitmentId,
            @RequestBody Map<String, String> requestBody) {

        String newDateString = requestBody.get("newDate");
        String newTimeString = requestBody.get("newTime");

        if(newDateString == null || newTimeString == null) {
            return ResponseEntity.badRequest().build();
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate newDate = LocalDate.parse(newDateString,dateFormatter);
        LocalTime newTime = LocalTime.parse(newTimeString,timeFormatter);

        Appoitment updatedAppoitment = doctorService.updateReservationByDoctor(appoitmentId,newDate,newTime);
        return ResponseEntity.ok(updatedAppoitment);
    }

    @DeleteMapping("/appoitments/{appoitmentId}")
    public ResponseEntity<Void> deleteAppoitment(@PathVariable Long appoitmentId) {

        doctorService.deleteAppoitment(appoitmentId);
        return ResponseEntity.noContent().build();
    }


}
