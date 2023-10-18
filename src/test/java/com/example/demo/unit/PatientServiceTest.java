package com.example.demo.unit;

import com.example.demo.exception.*;
import com.example.demo.model.Appointment;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.PatientService;
import com.example.demo.service.PhysicianService;
import com.example.demo.service.ServicesListService;
import com.example.demo.utils.AppointmentFactory;
import com.example.demo.utils.PatientFactory;
import com.example.demo.utils.ServiceFactory;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PatientServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PhysicianService physicianService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createPatient_valid() {

        // create a valid patient using the factory
        User providedPatient = PatientFactory.createPatient();

        // mock behavior of UserRepository.existByUsername
        when(userRepository.existsByUsername(providedPatient.getUsername())).thenReturn(false);

        // mock behavior of PasswordEncoder.encode
        when(passwordEncoder.encode(providedPatient.getPassword())).thenReturn("my_secret_password");

        // mock behavior of UserRepository.save
        when(userRepository.save(providedPatient)).thenReturn(providedPatient);

        User expectedPatient = patientService.createPatient(providedPatient);

        // Verify that userRepository.save were called
        verify(userRepository, times(1)).save(providedPatient);

        // Assert that the created patient is not null and is equal to the valid patient
        assertNotNull(expectedPatient);
        assertEquals(providedPatient, expectedPatient);

    }

    @Test
    public void createPatient_null(){

        User providedPatient = null;

        PatientNullException exception = assertThrows(PatientNullException.class, ()->patientService.createPatient(providedPatient));

        String expectedMessage = "Patient not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(providedPatient);

    }

    @ParameterizedTest
    @CsvSource({"name","surname","dateOfBirth","nationality","city","postalCode","street",
                "email", "telephone", "username", "role", "password"})
    public void createPatient_nullField(String nullField){

        User providedPatient = PatientFactory.createPatient();

        switch(nullField){

            case "name": providedPatient.setName(null);
            break;

            case "surname": providedPatient.setSurname(null);
            break;

            case "dateOfBirth": providedPatient.setDateOfBirth(null);
            break;

            case "nationality": providedPatient.setNationality(null);
            break;

            case "city": providedPatient.setCity(null);
            break;

            case "postalCode": providedPatient.setPostalCode(null);
            break;

            case "street": providedPatient.setStreet(null);
            break;

            case "email": providedPatient.setEmail(null);
            break;

            case "telephone": providedPatient.setTelephone(null);
            break;

            case "username": providedPatient.setUsername(null);
            break;

            case "role": providedPatient.setRole(null);
            break;

            case "password": providedPatient.setPassword(null);
            break;

        }

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class,
                ()->patientService.createPatient(providedPatient));

        String expectedMessage = "Required fields for creation profile are not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(providedPatient);

    }

    @Test
    public void createPatient_sameUsername(){

        User providedPatient = PatientFactory.createPatient();

        // mock behavior UserRepository.existByUsername
        when(userRepository.existsByUsername(providedPatient.getUsername()))
                .thenReturn(true);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class,
                ()->patientService.createPatient(providedPatient));

        String expectedMessage = "User with the same username already exists";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(providedPatient);

    }

    @Test
    public void reserveAppointment_valid(){

        Appointment providedReservedAppointment = AppointmentFactory.createReservedAppointment();

        Long providedAppointmentId = providedReservedAppointment.getId();

        // mock behavior AppointmentRepository.findById
        when(appointmentRepository.findById(providedAppointmentId))
                .thenReturn(Optional.of(providedReservedAppointment));

        User providedPatient = providedReservedAppointment.getPatient();

        // mock behavior AppointmentService.checkAppointmentIsOccupied
        when(appointmentService.checkAppointmentIsOccupied(providedPatient))
                .thenReturn(false);

        Long providedPatientId = providedPatient.getId();

        // mock behavior UserRepository.findById
        when(userRepository.findById(providedPatientId))
                .thenReturn(Optional.ofNullable(providedReservedAppointment.getPatient()));

        providedReservedAppointment.setPatient(providedPatient);

        Long providedPhysicianId = providedReservedAppointment.getPhysician().getId();

        Services providedService = ServiceFactory.createService_1();
        Long providedServiceId = providedService.getId();

        // mock behavior PhysicianService.checkServiceToPhysician
        when(physicianService.checkServiceToPhysician(providedPhysicianId, providedServiceId))
                .thenReturn(true);

        // mock behavior AppointmentRepository.save
        when(appointmentRepository.save(providedReservedAppointment))
                .thenReturn(providedReservedAppointment);

        Appointment expectedReservedAppointment = patientService.reserveAppointment(providedPatientId,
                providedAppointmentId,providedReservedAppointment.getService());

        assertNotNull(expectedReservedAppointment);
        assertNotNull(expectedReservedAppointment.getPatient());
        assertNotNull(expectedReservedAppointment.getService());
        assertEquals(expectedReservedAppointment,providedReservedAppointment);

        verify(appointmentRepository,times(1)).save(providedReservedAppointment);

    }

    @Test
    public void reserveAppointment_appointmentNotFound(){

        Appointment providedNotExistAppointment = null;

        Appointment providedReservedAppointment = AppointmentFactory.createReservedAppointment();
        Long providedReservedAppointmentId = providedReservedAppointment.getId();

        User providedPatient = providedReservedAppointment.getPatient();
        Services providedService = providedReservedAppointment.getService();

        // mock behavior AppointmentRepository.findById
        when(appointmentRepository.findById(providedReservedAppointmentId))
                .thenReturn(Optional.ofNullable(providedNotExistAppointment));

        AppointmentNotFoundException exception = assertThrows(AppointmentNotFoundException.class,
                ()->patientService.reserveAppointment(providedReservedAppointmentId,providedPatient.getId(),
                        providedService));

        String expectedMessage = "Appointment not found";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository,times(0)).save(providedReservedAppointment);

    }

    @Test
    public void reserveAppointment_patientNotFound(){

        Appointment providedReservedAppointment = AppointmentFactory.createReservedAppointment();
        Long providedReservedAppointmentId = providedReservedAppointment.getId();

        User providedPatient = providedReservedAppointment.getPatient();
        Long providedPatientId = providedPatient.getId();

        Services providedService = providedReservedAppointment.getService();

        User providedNotExistPatient = null;

        // mock behavior AppointmentRepository.findById
        when(appointmentRepository.findById(providedReservedAppointmentId))
                .thenReturn(Optional.of(providedReservedAppointment));

        // mock behavior UserRepository.findById
        when(userRepository.findById(providedPatientId))
                .thenReturn(Optional.ofNullable(providedNotExistPatient));

        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                ()->patientService.reserveAppointment(providedReservedAppointmentId,providedPatientId,
                        providedService));

        String expectedMessage = "Patient not found";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository,times(0)).save(providedReservedAppointment);

    }

    @Test
    public void reserveAppointment_appointmentIsOccupiedByOtherPatient(){

        Appointment providedReservedAppointment = AppointmentFactory.createReservedAppointment();
        Long providedReservedAppointmentId = providedReservedAppointment.getId();

        User providedPatient = providedReservedAppointment.getPatient();
        Long providedPatientId = providedPatient.getId();

        Services providedService = providedReservedAppointment.getService();

        // mock behavior AppointmentRepository.findById
        when(appointmentRepository.findById(providedReservedAppointmentId))
                .thenReturn(Optional.of(providedReservedAppointment));

        // mock behavior UserRepository.findById
        when(userRepository.findById(providedPatientId))
                .thenReturn(Optional.of(providedPatient));

        // mock behavior AppointmentService.checkAppointmentIsOccupied
        when(appointmentService.checkAppointmentIsOccupied(providedPatient))
                .thenReturn(true);

        AppointmentNotAvailableException exception = assertThrows(AppointmentNotAvailableException.class,
                ()->patientService.reserveAppointment(providedReservedAppointmentId, providedPatientId,providedService));

        String expectedMessage = "Appointment is not available for chosen date";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository,times(0)).save(providedReservedAppointment);

    }

    @Test
    public void reserveAppointment_serviceNotProvided(){

        Appointment providedReservedAppointment = AppointmentFactory.createReservedAppointment();
        Long providedReservedAppointmentId = providedReservedAppointment.getId();

        User providedPatient = providedReservedAppointment.getPatient();
        Long providedPatientId = providedPatient.getId();

        providedReservedAppointment.setService(null);

        // mock behavior AppointmentRepository.findById
        when(appointmentRepository.findById(providedReservedAppointmentId))
                .thenReturn(Optional.of(providedReservedAppointment));

        // mock behavior UserRepository.findById
        when(userRepository.findById(providedPatientId))
                .thenReturn(Optional.of(providedPatient));

        // mock behavior AppointmentService.checkAppointmentIsOccupied
        when(appointmentService.checkAppointmentIsOccupied(providedPatient))
                .thenReturn(false);

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class,
                ()->patientService.reserveAppointment(providedReservedAppointmentId,providedPatientId,
                        providedReservedAppointment.getService()));

        String expectedMessage = "Required fields are not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository,times(0)).save(providedReservedAppointment);

    }

    @Test
    public void reserveAppointment_chosenServiceNotBelongsToPhysician(){

        Appointment providedReservedAppointment = AppointmentFactory.createReservedAppointment();

        Long providedAppointmentId = providedReservedAppointment.getId();

        // mock behavior AppointmentRepository.findById
        when(appointmentRepository.findById(providedAppointmentId))
                .thenReturn(Optional.of(providedReservedAppointment));

        User providedPatient = providedReservedAppointment.getPatient();

        // mock behavior AppointmentService.checkAppointmentIsOccupied
        when(appointmentService.checkAppointmentIsOccupied(providedPatient))
                .thenReturn(false);

        Long providedPatientId = providedPatient.getId();

        // mock behavior UserRepository.findById
        when(userRepository.findById(providedPatientId))
                .thenReturn(Optional.ofNullable(providedReservedAppointment.getPatient()));

        providedReservedAppointment.setPatient(providedPatient);

        Long providedPhysicianId = providedReservedAppointment.getPhysician().getId();

        Services providedService = providedReservedAppointment.getService();
        Long providedServiceId = providedService.getId();

        // mock behavior PhysicianService.checkServiceToPhysician
        when(physicianService.checkServiceToPhysician(providedPhysicianId, providedServiceId))
                .thenReturn(false);

        ServiceNotFoundException exception = assertThrows(ServiceNotFoundException.class,
                ()->patientService.reserveAppointment(providedAppointmentId,providedPatientId,providedService));

        String expectedMessage = "Chosen service is not available by this physician";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository,times(0)).save(providedReservedAppointment);

    }

}
