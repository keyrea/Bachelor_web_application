package com.example.demo.unit;

import com.example.demo.exception.*;
import com.example.demo.model.Appointment;
import com.example.demo.model.Organization;
import com.example.demo.model.Services;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.OrganizationService;
import com.example.demo.service.ServicesListService;
import com.example.demo.utils.AppointmentFactory;
import com.example.demo.utils.OrganizationFactory;
import com.example.demo.utils.PhysicianFactory;
import com.example.demo.utils.ServiceFactory;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ServicesListService servicesListService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AdminService adminService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createPhysician_valid() {

        // Create a mock organization
        Organization organization = OrganizationFactory.createOrganization();

        // Create a physician with services using PhysicianFactory
        User providedPhysician = PhysicianFactory.createPhysician();

        // Mock behavior of organizationService.findById
        when(organizationService.findById(anyLong())).thenReturn(organization);

        // Mock behavior of userRepository.existByUsername
        when(userRepository.existsByUsername(providedPhysician.getUsername()))
                .thenReturn(false);

        // Mock behavior of passwordEncoder.encode
        when(passwordEncoder.encode(providedPhysician.getPassword()))
                .thenReturn("my_secret_password");

        // Mock behavior of userRepository.save
        when(userRepository.save(providedPhysician)).thenReturn(providedPhysician);

        // Create a list of mock services using ServiceFactory
        List<Services> mockServicesList = new ArrayList<>();
        mockServicesList.add(ServiceFactory.createService_1());
        mockServicesList.add(ServiceFactory.createService_2());

        // Mock behavior of servicesListService
        when(servicesListService.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
            Long serviceId = invocation.getArgument(0);
            return mockServicesList.stream()
                    .filter(service -> service.getId().equals(serviceId))
                    .findFirst()
                    .orElse(null);
        });

        // Call the method under test
        User expectedPhysician = adminService.createPhysician(providedPhysician);

        // Verify that userRepository.save and passwordEncoder.encode were called
        verify(userRepository, times(1)).save(providedPhysician);
        verify(passwordEncoder, times(1)).encode(providedPhysician.getPassword());

        // Assert that the expected physician is not null and is equal to the provided physician
        assertNotNull(expectedPhysician);
        assertEquals(expectedPhysician, providedPhysician);

    }

    @Test
    public void createPhysician_null(){

        User providedPhysician = null;

        // Use assertThrows to check for the exception and capture it
        PhysicianNullException exception = assertThrows(PhysicianNullException.class,
                () -> adminService.createPhysician(providedPhysician));

        String expectedMessage = "Physician not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(providedPhysician);

    }

    @ParameterizedTest
    @CsvSource({"name","surname","speciality","language","email","telephone",
            "username","role","password"})
    public void createPhysician_nullField(String nullField){

        User providedPhysician = PhysicianFactory.createPhysician();

        switch (nullField) {

            case "name": providedPhysician.setName(null);
            break;

            case "surname": providedPhysician.setSurname(null);
            break;

            case "speciality": providedPhysician.setSpeciality(null);
            break;

            case "language": providedPhysician.setLanguage(null);
            break;

            case "email": providedPhysician.setEmail(null);
            break;

            case "telephone": providedPhysician.setTelephone(null);
            break;

            case "username": providedPhysician.setUsername(null);
            break;

            case "role": providedPhysician.setRole(null);
            break;

            case "password": providedPhysician.setPassword(null);
            break;

            case "organization": providedPhysician.setOrganization(null);
            break;

            case "services": providedPhysician.setServices(null);
            break;

        }

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class,
                () -> adminService.createPhysician(providedPhysician));

        String expectedMessage = "Required fields are not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(providedPhysician);

    }

    @Test
    public void createPhysician_sameUsername(){

        // Create a mock organization
        Organization organization = OrganizationFactory.createOrganization();

        // Create a physician with services using PhysicianFactory
        User providedPhysician = PhysicianFactory.createPhysician();

        // Mock behavior of organizationService.findById
        when(organizationService.findById(anyLong())).thenReturn(organization);

        // Mock behavior of userRepository.existByUsername
        when(userRepository.existsByUsername(providedPhysician.getUsername())).thenReturn(true);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class,
                ()->adminService.createPhysician(providedPhysician));

        String expectedMessage = "Physician with the same username already exists";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(providedPhysician);

    }

    @Test
    public void createAppointment_valid(){

        Appointment providedAppointment = AppointmentFactory.createEmptyAppointment();

        // mock behavior AppointmentService.isDateOrTimeIsDeprecated
        when(appointmentService.isDateOrTimeOfAppointmentDeprecated(providedAppointment))
                .thenReturn(false);

        // mock behavior UserRepository.findById
        when(userRepository.findById(providedAppointment.getPhysician().getId()))
                .thenReturn(Optional.ofNullable(providedAppointment.getPhysician()));

        // mock behavior AppointmentRepository.save
        when(appointmentRepository.save(providedAppointment)).thenReturn(providedAppointment);

        Appointment expectedAppointment = adminService.createAppointment(providedAppointment);

        verify(appointmentRepository, times(1)).save(providedAppointment);

        assertNotNull(expectedAppointment);
        assertEquals(providedAppointment,expectedAppointment);

    }

    @Test
    public void createAppointment_null(){

        Appointment providedAppointment = null;

        AppointmentNullException exception = assertThrows(AppointmentNullException.class,
                () -> adminService.createAppointment(providedAppointment));

        String expectedMessage = "Appointment not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository, times(0)).save(providedAppointment);

    }

    @ParameterizedTest
    @CsvSource({"date","time"}) // instead physician will be provided null field
    public void createAppointment_nullField(String nullField){

        Appointment providedAppointment = AppointmentFactory.createEmptyAppointment();

        switch (nullField){

            case "date": providedAppointment.setDate(null);
            break;

            case "time": providedAppointment.setTime(null);
            break;

            case "physician": providedAppointment.setPhysician(null);
            break;

        }

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class,
                () -> adminService.createAppointment(providedAppointment));

        String expectedMessage = "Required fields are not provided";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository, times(0)).save(providedAppointment);

    }

    @Test
    public void createAppointment_deprecatedDateOrTime(){

        Appointment providedAppointment = AppointmentFactory.createEmptyAppointment();

        // mock behavior AppointmentService.isDateOrTimeOfAppointmentDeprecated
        when(appointmentService.isDateOrTimeOfAppointmentDeprecated(providedAppointment)).thenReturn(true);

        InvalidAppointmentDateOrTimeException exception = assertThrows(InvalidAppointmentDateOrTimeException.class,
                ()->adminService.createAppointment(providedAppointment));

        String expectedMessage = "Chosen date or time is deprecated";
        String providedMessage = exception.getMessage();

        assertTrue(providedMessage.contains(expectedMessage));

        verify(appointmentRepository, times(0)).save(providedAppointment);

    }

}
