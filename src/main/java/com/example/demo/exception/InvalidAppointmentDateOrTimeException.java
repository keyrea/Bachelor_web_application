package com.example.demo.exception;

public class InvalidAppointmentDateOrTimeException extends RuntimeException{

    public InvalidAppointmentDateOrTimeException(String message) {
        super(message);
    }

}
