package com.Proyect.UserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyect.UserService.model.User;

/**
 * UserRepository
 * Interfaz Repository (DAO) para acceso a datos de usuarios.
 * Extiende JpaRepository para heredar operaciones CRUD automáticas.
 * Define consultas personalizadas para búsquedas específicas.
 */
@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su username (nombre de usuario).
     * Genera automáticamente la consulta SQL: SELECT * FROM users WHERE username = ?
     *
     * @param username El nombre de usuario a buscar
     * @return Optional<User> Contiene el usuario si existe, empty si no
     */
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

}
