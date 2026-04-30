package com.Proyect.UserService.model;

import java.math.BigDecimal;

public class UserResponseDTO {

    private final Long id;
    private final String nombre;
    private final String username;
    private final BigDecimal balance;

    public UserResponseDTO(Long id, String nombre, String username, BigDecimal balance) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
