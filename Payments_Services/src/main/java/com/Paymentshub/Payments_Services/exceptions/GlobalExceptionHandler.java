package com.Paymentshub.Payments_Services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.Paymentshub.Payments_Services.models.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
    }
    @ExceptionHandler(InvalidUserIdException.class)
    public ErrorResponse handleInvalidUsername(InvalidUserIdException ex) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
    }
    @ExceptionHandler(UserServiceException.class)
    public ErrorResponse handleUserServiceException(UserServiceException ex) {
        return new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
    }

}
