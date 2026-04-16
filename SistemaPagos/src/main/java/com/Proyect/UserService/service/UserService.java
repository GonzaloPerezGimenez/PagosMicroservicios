package com.Proyect.UserService.service;

import java.util.List;

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
        return userRepository.findById(id).
        orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
    }

    private void validateUsername(String username) {
        if(username == null || username.isBlank()) {
            throw new InvalidUsernameException("El nombre de usuario no puede estar vacío");
        }
        if(userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExist("El nombre de usuario ya existe");
        }
    }

}
