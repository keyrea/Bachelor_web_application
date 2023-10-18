package com.example.demo.service;

import com.example.demo.exception.AppointmentNotAvailableException;
import com.example.demo.exception.MissingRequiredFieldsException;
import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment updateReservation(Long userId, Long oldAppointmentId,
                                                    Long newAppointmentId){

        if(newAppointmentId==null){
            throw new MissingRequiredFieldsException("Appointment is not chosen");
        }

        Appointment newAppointment = appointmentRepository.findById(newAppointmentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"New appointment not " +
                        "found"));

        Appointment oldAppointment = appointmentRepository.findById(oldAppointmentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Old appointment not" +
                        "found"));

        if(newAppointment.getPatient()!=null){
            throw new AppointmentNotAvailableException("Chosen appointment is not available");
        }

        newAppointment.setPatient(oldAppointment.getPatient());
        newAppointment.setService(oldAppointment.getService());

        oldAppointment.setPatient(null);
        oldAppointment.setService(null);

        return appointmentRepository.save(newAppointment);

    }

    public void deleteAppointment(Long appointmentId){

        // fetch the appointment from Repository
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        // delete the appointment
        appointmentRepository.delete(appointment);

    }

    public boolean checkAppointmentIsOccupied(User patient) {
        if (patient != null) {
            return true; // Appointment is not available
        } else {
            return false;
        }
    }

    public boolean isDateOrTimeOfAppointmentDeprecated(Appointment appointment) {
        if (appointment.getDate().isBefore(LocalDate.now()) || appointment.getTime().isBefore(LocalTime.now())) {
            return true;
        } else {
            return false;
        }
    }

}
