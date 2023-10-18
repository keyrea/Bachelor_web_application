package com.example.demo.utils;

import com.example.demo.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentFactory {

    public static Appointment createEmptyAppointment(){

        Appointment appointment = new Appointment();

        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.now());
        appointment.setPatient(null);
        appointment.setPhysician(PhysicianFactory.createPhysician());
        appointment.setService(null);

        return appointment;
    }

    public static Appointment createReservedAppointment(){

        Appointment appointment = new Appointment();

        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.now());
        appointment.setPatient(PatientFactory.createPatient());
        appointment.setPhysician(PhysicianFactory.createPhysician());
        appointment.setService(ServiceFactory.createService_1());

        return appointment;

    }

}
