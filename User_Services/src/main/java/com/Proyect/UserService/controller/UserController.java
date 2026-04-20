package com.Proyect.UserService.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Proyect.UserService.model.User;
import com.Proyect.UserService.service.UserService;

import jakarta.validation.Valid;

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
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    } 

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }
    
    @PostMapping("/{id}/debit")
    public User debitUserBalance(@PathVariable Long id, @RequestParam("amount") BigDecimal amount) {
        return userService.debitUserBalance(id, amount);
    }

    @PostMapping("/{id}/credit")
    public User creditUserBalance(@PathVariable Long id, @RequestParam("amount") BigDecimal amount) {
        return userService.creditUserBalance(id, amount);
    }
    
    @PatchMapping("/{id}/update")
    public User updateUserPatch(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        return userService.updateUser(id, updates);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


}
