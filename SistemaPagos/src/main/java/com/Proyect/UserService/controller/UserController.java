package com.Proyect.UserService.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proyect.UserService.model.User;
import com.Proyect.UserService.service.UserService;

/**
 * UserController
 * Controlador REST que gestiona las peticiones HTTP relacionadas con usuarios.
 * Expone endpoints para crear y recuperar usuarios.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor que inyecta el servicio de usuarios.
     * @param userService Servicio de lógica de negocio de usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return List<User> lista de todos los usuarios en formato JSON
     *
     * HTTP: GET /users
     */
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * Recibe los datos del usuario en el cuerpo de la petición (JSON).
     * Delega la validación y almacenamiento al servicio.
     *
     * @param user Objeto User con los datos del nuevo usuario (JSON del request)
     * @return User el usuario creado con su ID asignado
     * @throws IllegalArgumentException si hay validación fallida (username vacío o duplicado)
     *
     * HTTP: POST /users
     * Content-Type: application/json
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    
        
}
