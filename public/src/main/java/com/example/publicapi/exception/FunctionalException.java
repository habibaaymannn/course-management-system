package com.example.publicapi.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class FunctionalException extends RuntimeException {
    private final HttpStatus httpStatus;
    public FunctionalException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}