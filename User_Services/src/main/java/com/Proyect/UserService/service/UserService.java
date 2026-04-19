package com.Proyect.UserService.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Proyect.UserService.exceptions.InvalidUsernameException;
import com.Proyect.UserService.exceptions.UsernameAlreadyExist;
import com.Proyect.UserService.model.User;
import com.Proyect.UserService.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        validateUsername(user.getUsername());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return getExistingUser(id);
    }

    public User updateUser(Long id, Map<String, String> updates) {
        User user = getExistingUser(id);
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String campo = entry.getKey();
            String valor = entry.getValue();
            switch (campo) {
                case "nombre" -> user.setNombre(valor);
                case "username" -> {
                    validateUsername(valor);
                    user.setUsername(valor);
                }
                case "password" -> user.setPassword(valor);
                default -> throw new IllegalArgumentException("Campo no válido");
            }
        }
        return userRepository.save(user);
    }

    public User debitUserBalance(Long id, BigDecimal amount) {
        User user = getExistingUser(id);
        validateDebitAmount(user, amount);
        user.setBalance(user.getBalance().subtract(amount));
        return userRepository.save(user);
    }

    public User creditUserBalance(Long id, BigDecimal amount) {
        User user = getExistingUser(id);
        user.setBalance(user.getBalance().add(amount));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getExistingUser(id);       
        userRepository.delete(user);
    }

    // Métodos privados para validaciones y lógica interna

    private void validateUsername(String username) {
        if(username == null || username.isBlank()) {
            throw new InvalidUsernameException("El nombre de usuario no puede estar vacío");
        }
        if(userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExist("El nombre de usuario ya existe");
        }
    }
    private User getExistingUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + userId + " no encontrado"));
    }
    private void validateDebitAmount(User user, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el débito.");
        }
    }

}
