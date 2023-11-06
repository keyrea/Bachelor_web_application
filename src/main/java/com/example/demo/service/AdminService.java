package com.example.demo.service;

import com.example.demo.exception.*;
import com.example.demo.model.Appointment;
import com.example.demo.model.Organization;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ServicesListService servicesListService;

    @Autowired
    private AppointmentService appointmentService;

    public User createPhysician(User physician) {

        if(physician==null){
            throw new PhysicianNullException("Physician not provided");
        }

        // check if required fields for physician are provided
        if (physician.getName() == null || physician.getSurname() == null || physician.getSpeciality() == null
                || physician.getLanguage() == null || physician.getEmail() == null
                || physician.getTelephone() == null
                || physician.getUsername() == null || physician.getRole() == null
                || physician.getPassword() == null
                || physician.getOrganization() == null || physician.getServices().isEmpty()) {

            throw new MissingRequiredFieldsException("Required fields are not provided");

        }

        // check if a physician with similar username exist
        if (userRepository.existsByUsername(physician.getUsername())) {
            throw new DuplicateUserException("Physician with the same username already exists");
        }

        // compare organizationId of admin with organizationId entered for physician
        if(!organizationService.compareOrganizationIdOfAdminWithOrganizationIdOfPhysician(physician.getOrganization().getId())){
            throw new NotPermittedException("Admin does not have permission to create physician for this organization");
        }

        // fetch organization details based on the provided organization id
        Long organizationId = physician.getOrganization().getId();
        Organization organization = organizationService.findById(organizationId);

        // Fetch services based on the provided service id
        List<Services> services = new ArrayList<>();
        for (Services service : physician.getServices()) {
            Long serviceId = service.getId();
            Services fetchedService = servicesListService.findById(serviceId);
            if (fetchedService != null) {
                services.add(fetchedService);
            } else {
                // Handle the case where a service with the given ID does not exist
                throw new ServiceNotFoundException("Service with ID " + serviceId + " does not exist");
            }
        }

        physician.setOrganization(organization);
        physician.setServices(services);

        String encodedPassword = passwordEncoder.encode(physician.getPassword());
        physician.setPassword(encodedPassword);

        return userRepository.save(physician);

    }

    public User updatePhysician(User physician) {

        if(physician==null){
            throw new PhysicianNullException("Physician not provided");
        }

        // check if required fields for physician are provided
        if (physician.getName() == null || physician.getSurname() == null || physician.getSpeciality() == null
                || physician.getLanguage() == null || physician.getEmail() == null
                || physician.getTelephone() == null
                || physician.getUsername() == null || physician.getRole() == null
                || physician.getPassword() == null
                || physician.getOrganization() == null || physician.getServices() == null
                || physician.getServices().isEmpty()) {

            throw new MissingRequiredFieldsException("Required fields are not provided");

        }

        // compare organizationId of admin with organizationId entered for physician
        if(!organizationService.compareOrganizationIdOfAdminWithOrganizationIdOfPhysician(physician.getOrganization().getId())){
            throw new NotPermittedException("Admin does not have permission to create physician for this organization");
        }

        // fetch organization details based on the provided organization id
        Long organizationId = physician.getOrganization().getId();
        Organization organization = organizationService.findById(organizationId);

        // Fetch services based on the provided service id
        List<Services> services = new ArrayList<>();
        for (Services service : physician.getServices()) {
            Long serviceId = service.getId();
            Services fetchedService = servicesListService.findById(serviceId);
            if (fetchedService != null) {
                services.add(fetchedService);
            } else {
                // Handle the case where a service with the given ID does not exist
                throw new RuntimeException("Service with ID " + serviceId + " does not exist");
            }
        }

        physician.setOrganization(organization);
        physician.setServices(services);

        String encodedPassword = passwordEncoder.encode(physician.getPassword());
        physician.setPassword(encodedPassword);
        return userRepository.save(physician);

    }

    public void deletePhysician(Long physicianId) {

        User physician = userRepository.findById(physicianId)
                        .orElseThrow(()->new PhysicianNotFoundException("Provided physician not found"));

        userRepository.deleteById(physicianId);

    }

    public Appointment createAppointment(Appointment appointment){

        if(appointment == null){
            throw new AppointmentNullException("Appointment not provided");
        }

        // check for missing required fields
        if (appointment.getDate() == null || appointment.getTime() == null
                || appointment.getPhysician() == null) {
            throw new MissingRequiredFieldsException("Required fields are not provided");
        }

        // check if the appointments date and time are deprecated
        if(appointmentService.isDateOrTimeOfAppointmentDeprecated(appointment)){
            throw new InvalidAppointmentDateOrTimeException("Chosen date or time is deprecated");
        }

        Long physicianId = appointment.getPhysician().getId();
        User physician = userRepository.findById(physicianId)
                .orElseThrow(()-> new PhysicianNotFoundException("Provided physician not found"));

        // set physician object
        appointment.setPhysician(physician);

        // set null values for patient and service
        appointment.setPatient(null);
        appointment.setService(null);

        return appointmentRepository.save(appointment);

    }

    public Appointment updateAppointment(Appointment appointment){

        if(appointment == null){
            throw new AppointmentNullException("Appointment not provided");
        }

        // check for missing required fields
        if (appointment.getDate() == null || appointment.getTime() == null
                || appointment.getPhysician() == null) {
            throw new MissingRequiredFieldsException("Required fields are not provided");
        }

        // check if the appointments date and time are deprecated
        if(appointmentService.isDateOrTimeOfAppointmentDeprecated(appointment)){
            throw new InvalidAppointmentDateOrTimeException("Chosen date or time is deprecated");
        }

        Long physicianId = appointment.getPhysician().getId();
        User physician = userRepository.findById(physicianId)
                .orElseThrow(()->new PhysicianNotFoundException("provided physician not found"));

        // set physician object
        appointment.setPhysician(physician);

        // set null values for patient and service
        appointment.setPatient(null);
        appointment.setService(null);

        return appointmentRepository.save(appointment);
    }

}

