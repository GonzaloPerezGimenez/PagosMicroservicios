package com.Proyect.UserService.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Proyect.UserService.config.JwtServices;
import com.Proyect.UserService.exceptions.UsernameAlreadyExist;
import com.Proyect.UserService.model.User;
import com.Proyect.UserService.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        validateUsername(user.getUsername());
        encodePassword(user);
        return userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        return logintoken(username, password);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return getExistingUserByID(id);
    }

    public User updateUser(Long id, Map<String, String> updates) {
        User user = getExistingUserByID(id);
        User updatedUser = applyUpdates(user, updates);
        return userRepository.save(updatedUser);
    }

    public User debitUserBalance(Long id, BigDecimal amount) {
        User user = getExistingUserByID(id);
        validateDebitAmount(user, amount);
        user.setBalance(user.getBalance().subtract(amount));
        return userRepository.save(user);
    }

    public User creditUserBalance(Long id, BigDecimal amount) {
        User user = getExistingUserByID(id);
        user.setBalance(user.getBalance().add(amount));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getExistingUserByID(id);       
        userRepository.delete(user);
    }

    // Métodos privados para validaciones y lógica interna

    private void validateUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExist("El nombre de usuario ya existe");
        }
    }
    private void validatePassword(String password, String encodedPassword) {
       if(!passwordEncoder.matches(password, encodedPassword)) {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }
    private User getExistingUserByID(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + userId + " no encontrado"));
    }
    private User getExistingUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario con nombre de usuario " + username + " no encontrado"));
    }
    private void validateDebitAmount(User user, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el débito.");
        }
    }
    private String logintoken(String username, String password) {
        User user = getExistingUserByUserName(username);
        validatePassword(password, user.getPassword());
        return JwtServices.generateToken(username);
    }
    private User applyUpdates(User user, Map<String, String> updates) {
        validateUsername(user.getUsername());
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String campo = entry.getKey();
            String valor = entry.getValue();
            switch (campo) {
                case "nombre" -> user.setNombre(valor);
                case "username" -> {
                    validateUsername(valor);
                    user.setUsername(valor);
                }
                case "password" -> encodePassword(user);
                default -> throw new IllegalArgumentException("Campo no válido");
            }
        }
        return user;
    }
    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

}
