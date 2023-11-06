package com.example.demo.service;

import com.example.demo.exception.AppointmentNotAvailableException;
import com.example.demo.exception.AppointmentNotBelongsToUserException;
import com.example.demo.exception.AppointmentNotFoundException;
import com.example.demo.exception.MissingRequiredFieldsException;
import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private HttpServletRequest request;

    public Appointment updateReservation(Long userId, Long oldAppointmentId,
                                                    Long newAppointmentId){

        if(newAppointmentId==null){
            throw new MissingRequiredFieldsException("Appointment is not chosen");
        }

        Appointment newAppointment = appointmentRepository.findById(newAppointmentId)
                .orElseThrow(()->new AppointmentNotFoundException("New appointment not found"));

        Appointment oldAppointment = appointmentRepository.findById(oldAppointmentId)
                .orElseThrow(()->new AppointmentNotFoundException("Old appointment not found"));

        if(!belongsAppointmentToUser(userId,oldAppointmentId)){
            throw new AppointmentNotBelongsToUserException("This appointment cannot be managed");
        }

        if(newAppointment.getPatient()!=null){
            throw new AppointmentNotAvailableException("Chosen appointment is not available");
        }

        newAppointment.setPatient(oldAppointment.getPatient());
        newAppointment.setService(oldAppointment.getService());

        oldAppointment.setPatient(null);
        oldAppointment.setService(null);

        return appointmentRepository.save(newAppointment);

    }

    public void deleteAppointment(Long physicianId, Long appointmentId){

        // fetch the appointment from Repository
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        if(!belongsAppointmentToUser(physicianId,appointmentId)){
            throw new AppointmentNotBelongsToUserException("This appointment cannot be managed");
        }

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

        LocalDate chosenDate = appointment.getDate();
        LocalTime chosenTime = appointment.getTime();

        if (chosenDate.isBefore(LocalDate.now())) {
            return true;
        } else {
            if(chosenDate == LocalDate.now()){
                if(chosenTime.isBefore(LocalTime.now())){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public boolean belongsAppointmentToUser(Long userId, Long appointmentId){

        // find role of user via userId
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found with Id"+userId));
        String userRole = user.getRole();

        // get JWT token from request header
        String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = requestTokenHeader.substring("Bearer ".length());

        // extract organization id from JWT token
        Long physicianOrganizationId = jwtTokenUtil.extractOrganizationId(jwtToken);

        // find appointment with appointmentId
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));

        // retrieve organization id from extracted physician
        Long organizationId = appointment.getPhysician().getOrganization().getId();

        if(userRole.equals("PHYSICIAN")){
            // retrieve physician id from extracted appointment
            Long physicianIdFromAppointment = appointment.getPhysician().getId();
            if(userId == physicianIdFromAppointment && physicianOrganizationId == organizationId){
                return true;
            }
        }

        else if (userRole.equals("PATIENT")) {
            // retrieve patient id from extracted appointment
            Long patientIdFromAppointment = appointment.getPatient().getId();
            if(userId == patientIdFromAppointment) {
                return true;
            }
        }

        return false;
    }

}
