package com.Proyect.UserService.exceptions;

public class UsernameAlreadyExist extends RuntimeException {
    public UsernameAlreadyExist(String message) {
        super(message);
    }

}
