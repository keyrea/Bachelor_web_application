package com.example.demo.exception;

public class AppointmentNotAvailableException extends RuntimeException{

    public AppointmentNotAvailableException(String message) {
        super(message);
    }

}
