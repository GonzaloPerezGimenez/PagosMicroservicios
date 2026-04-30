package com.Proyect.UserService.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Proyect.UserService.config.JwtServices;
import com.Proyect.UserService.exceptions.UsernameAlreadyExist;
import com.Proyect.UserService.model.User;
import com.Proyect.UserService.model.UserResponseDTO;
import com.Proyect.UserService.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServices jwtServices;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtServices jwtServices) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtServices = jwtServices;
    }

    public ResponseEntity<String> saveUser(User user) {
        userRepository.save(registerUser(user));
        return ResponseEntity.ok("Usuario registrado con éxito.");
    }

    public String loginUser(String username, String password) {
        return logintoken(username, password);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                user.getId(),
                user.getNombre(),
                user.getUsername(),
                user.getBalance()
        ))
                .toList();
    }

    public User getUserById(Long id) {
        return getExistingUserByID(id);
    }

    public ResponseEntity<String> updateUser(Long id, Map<String, String> updates) {
        userRepository.save(applyUpdates(getExistingUserByID(id), updates));
        return ResponseEntity.ok("Usuario actualizado con éxito.");
    }

    public ResponseEntity<String> debitUserBalance(Long id, BigDecimal amount) {
        userRepository.save(applyDebit(getExistingUserByID(id), amount));
        return ResponseEntity.ok("Pago realizado con éxito.");
    }

    public ResponseEntity<String> creditUserBalance(Long id, BigDecimal amount) {
        userRepository.save(applyCredit(getExistingUserByID(id), amount));
        return ResponseEntity.ok("Cobro realizado con éxito.");
    }

    public ResponseEntity<String> deleteUser(Long id) {
        userRepository.delete(getExistingUserByID(id));
        return ResponseEntity.ok("Usuario eliminado con éxito.");
    }

    // Métodos privados para validaciones y lógica interna
    private void validateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExist("El nombre de usuario ya existe");
        }
    }

    private void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }

    private User registerUser(User user) {
        validateUsername(user.getUsername());
        encodePassword(user);
        return user;
    }

    private User getExistingUserByID(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario con ID " + userId + " no encontrado"
        ));

    }

    private User getExistingUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario con nombre de usuario " + username + " no encontrado"
        ));
    }

    private void validateDebitAmount(User user, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el débito.");
        }
    }

    private String logintoken(String username, String password) {
        User user = getExistingUserByUserName(username);
        validatePassword(password, user.getPassword());
        return jwtServices.generateToken(username);
    }

    private User applyUpdates(User user, Map<String, String> updates) {
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String campo = entry.getKey();
            String valor = entry.getValue();
            switch (campo) {
                case "nombre" ->
                    user.setNombre(valor);
                case "username" -> {
                    validateUsername(valor);
                    user.setUsername(valor);
                }
                case "password" ->
                    encodePassword(user);
                default ->
                    throw new IllegalArgumentException("Campo no válido");
            }
        }
        return user;
    }

    private User applyDebit(User user, BigDecimal amount) {
        validateDebitAmount(user, amount);
        user.setBalance(user.getBalance().subtract(amount));
        return user;
    }

    private User applyCredit(User user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        return user;
    }

    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

}
