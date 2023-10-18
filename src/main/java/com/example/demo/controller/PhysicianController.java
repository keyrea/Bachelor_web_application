package com.example.demo.controller;

import com.example.demo.exception.InvalidAppointmentDateOrTimeException;
import com.example.demo.exception.MissingRequiredFieldsException;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.PhysicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/physician")
@PreAuthorize("hasRole('PHYSICIAN')")
public class PhysicianController {

    @Autowired
    private PhysicianService physicianService;

    @Autowired
    private AppointmentService appointmentService;

    @PutMapping("/{physicianId}/appointments/{oldAppointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> updateReservationByPhysician(
            @PathVariable Long physicianId,
            @PathVariable Long oldAppointmentId,
            @RequestBody Map<String, String> requestBody) {

        try {

            String newAppointmentIdString = requestBody.get("newAppointmentId");

            Long newAppointmentId = Long.parseLong(newAppointmentIdString);

            Appointment updatedAppointment = appointmentService.updateReservation(physicianId,
                    oldAppointmentId, newAppointmentId);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                    .success("Appointment updated successfully", updatedAppointment));

        }

        catch (MissingRequiredFieldsException | InvalidAppointmentDateOrTimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse
                    .error("Failed to update appointment: "+e.getMessage()));

        }

    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long appointmentId) {

        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Appointment is deleted successfully", null));
    }

}
