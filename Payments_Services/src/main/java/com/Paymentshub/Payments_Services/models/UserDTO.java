package com.Paymentshub.Payments_Services.models;

import java.math.BigDecimal;

public class UserDTO {

    private Long id;

    /** Nombre completo del usuario */
    private String nombre;

    /** Nombre de usuario único para login - No puede ser nulo ni duplicado */
    private String username;

    /** Contraseña del usuario (idealmente debería estar encriptada) */
    private String password;

    private BigDecimal balance;

    /**
     * Constructor completo que inicializa los datos básicos del usuario.
     *
     * @param id Identificador único del usuario
     * @param nombre Nombre completo del usuario
     * @param username Nombre de usuario para login
     * @param password Contraseña del usuario
     */
    public UserDTO(String nombre, String username, String password) {

        this.nombre=nombre;
        this.username=username;
        this.password=password;
    }

    /**
     * Obtiene el ID del usuario.
     * @return El identificador único del usuario
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del usuario.
     * @param id El identificador único a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre completo del usuario.
     * @return El nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre completo del usuario.
     * @param nombre El nombre a asignar
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el nombre de usuario para login.
     * @return El username del usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario para login.
     * @param username El username a asignar
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return La contraseña del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     * @param password La contraseña a asignar
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}