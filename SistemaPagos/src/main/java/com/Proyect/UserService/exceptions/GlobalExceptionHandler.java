package com.Proyect.UserService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.Proyect.UserService.model.ErrorResponse;

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
    @ExceptionHandler(UsernameAlreadyExist.class)
    public ErrorResponse handleUsernameAlreadyExists(UsernameAlreadyExist ex) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
    }
    @ExceptionHandler(InvalidUsernameException.class)
    public ErrorResponse handleInvalidUsername(InvalidUsernameException ex) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                java.time.LocalDateTime.now()
        );
    }

}
