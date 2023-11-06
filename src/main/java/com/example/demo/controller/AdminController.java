package com.example.demo.controller;

import com.example.demo.exception.*;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AppointmentService appointmentService;

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("physician/create")
    public ResponseEntity<ApiResponse<User>> createPhysician(@RequestBody User physician) {

        logger.info("Received request to create a new physician: {}", physician);

        try {

            User createdPhysician = adminService.createPhysician(physician);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Physician created successfully", physician));

        }

        catch (MissingRequiredFieldsException | DuplicateUserException
               | ServiceNotFoundException | PhysicianNullException | NotPermittedException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create physician: " + e.getMessage()));

        }

    }

    @PutMapping("/physician/update/{id}")
    public ResponseEntity<ApiResponse<User>> updatePhysician(@PathVariable Long id, @RequestBody User physician) {

        logger.info("Received request to update physician with ID {}: {}", id, physician);

        try {

            physician.setId(id); // Set the id for Update
            User updatedPhysician = adminService.updatePhysician(physician);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                    .success("Physician updated successfully", updatedPhysician));

        }

        catch (MissingRequiredFieldsException | DuplicateUserException
               | ServiceNotFoundException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update physician: " + e.getMessage()));

        }

    }

    @DeleteMapping("/physician/delete/{physicianId}")
    public ResponseEntity<ApiResponse<Void>> deletePhysician(@PathVariable Long physicianId) {

        adminService.deletePhysician(physicianId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Physician is deleted successfully", null));

    }

    @PostMapping("/appointment/create")
    public ResponseEntity <ApiResponse<Appointment>> createAppointment(@RequestBody Appointment appointment) {

        logger.info("Received request to create/update an appointment: {}", appointment);

        try {

            Appointment savedAppointment = adminService.createAppointment(appointment);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Appointment created successfully", savedAppointment));

        }

        catch (MissingRequiredFieldsException | InvalidAppointmentDateOrTimeException
               | PhysicianNotFoundException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create appointment: "+e.getMessage()));

        }

    }

    @PutMapping("/appointment/update/{appointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointment(
            @PathVariable Long appointmentId,
            @RequestBody Appointment appointment) {

        logger.info("Received request to update an appointment with ID {}: {}", appointmentId, appointment);

        try {

            Appointment updatedAppointment = adminService.updateAppointment(appointment);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success("Appointment updated successfully", updatedAppointment));

        }

        catch (MissingRequiredFieldsException | InvalidAppointmentDateOrTimeException
               | PhysicianNotFoundException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update appointment: "+e.getMessage()));

        }

    }

    @DeleteMapping("/appointment/delete/{physicianId}/{appointmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(
            @PathVariable Long physicianId,
            @PathVariable Long appointmentId) {

        try {
            appointmentService.deleteAppointment(physicianId, appointmentId);

            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(ApiResponse.success("Appointment is deleted successfully", null));
        }

        catch (AppointmentNotFoundException | AppointmentNotBelongsToUserException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error to delete appointment: "+e.getMessage()));

        }

    }

}
