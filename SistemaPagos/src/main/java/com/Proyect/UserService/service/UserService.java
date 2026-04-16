package com.Proyect.UserService.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Proyect.UserService.exceptions.InvalidUsernameException;
import com.Proyect.UserService.exceptions.UsernameAlreadyExist;
import com.Proyect.UserService.model.User;
import com.Proyect.UserService.repository.UserRepository;

/**
 * UserService
 * Servicio de lógica de negocio para la gestión de usuarios.
 * Realiza validaciones, operaciones CRUD y comunicación con la base de datos.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * @param userRepository Repositorio para acceso a datos de usuarios
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     * Valida que el username no sea nulo/vacío y que no exista duplicado.
     *
     * @param user Objeto User con los datos del nuevo usuario
     * @return User el usuario creado y recuperado de la BD
     * @throws InvalidUsernameException si el username está vacío o ya existe
     */
    public User saveUser(User user) {
        String username= user.getUsername();
        if(username == null || user.getUsername().isBlank()) {
            throw new InvalidUsernameException("El nombre de usuario no puede estar vacío");
        }
        if(userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExist("El nombre de usuario ya existe");
        }
        return userRepository.save(user);
    }

    /**
     * Obtiene la lista completa de todos los usuarios registrados.
     *
     * @return List<User> lista con todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario específico por su ID.
     *
     * @param id Identificador único del usuario
     * @return User el usuario encontrado, o null si no existe
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).
        orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
    }

}
