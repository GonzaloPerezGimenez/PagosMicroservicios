package com.Proyect.UserService.model;

import java.math.BigDecimal;

import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User
 * Entidad JPA que representa un usuario en el sistema.
 * Se mapea a la tabla "users" en la base de datos.
 */

@SoftDelete(strategy=SoftDeleteType.ACTIVE, columnName="is_active")
@Entity
@Table(name = "users")


public class User {

    /** Identificador único del usuario (ID primaria) - Generado automáticamente */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario */
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    /** Nombre de usuario único para login - No puede ser nulo ni duplicado */
    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    /** Contraseña del usuario (idealmente debería estar encriptada) */
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    private BigDecimal balance;


    /**
     * Constructor completo que inicializa los datos básicos del usuario.
     *
     * @param nombre Nombre completo del usuario
     * @param username Nombre de usuario para login
     * @param password Contraseña del usuario
     */
    public User(String nombre, String username, String password){
        this.nombre=nombre;
        this.username=username;
        this.password=password;
        this.balance=BigDecimal.ZERO;
    }

    /**
     * Constructor vacío requerido por Hibernate/JPA.
     */
    public User() {
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
     * @param Id El identificador único a asignar
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
