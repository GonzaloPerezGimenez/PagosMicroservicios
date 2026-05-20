package com.Proyect.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Proyect.UserService.config.JwtServices;
import com.Proyect.UserService.exceptions.UsernameAlreadyExist;
import com.Proyect.UserService.model.User;
import com.Proyect.UserService.repository.UserRepository;
import com.Proyect.UserService.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceAppTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServices jwtServices;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Test encriptacion de contraseña al registrar usuario")
    void saveUser_ShouldEncryptPassword() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainPassword");
        when(passwordEncoder.encode("plainPassword")).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.saveUser(user);

        // Then
        assertEquals("encryptedPassword", user.getPassword());
        verify(userRepository).save(user);

    }

    @Test
    @DisplayName("Test para registrar un usuario")
    void saveUser() {
        // Arrange
        User user = new User("testuser", "testUsername", "plainPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        // When
        ResponseEntity<String> result = userService.saveUser(user);

        // Then
        assertEquals(ResponseEntity.ok("Usuario registrado con éxito."), result);
        verify(userRepository).save(user);

    }

    @Test
    @DisplayName("Test para validar que falla al registrar un usuario con username ya existente")
    void validateUserRegistration_Fails_WhenUsernameAlreadyExists() {
        // Arrange
        User user = new User("testuser", "testUsername", "plainPassword");

        when(userRepository.findByUsername("testUsername"))
                .thenReturn(Optional.of(user));

        // When
        UsernameAlreadyExist exception = assertThrows(UsernameAlreadyExist.class, () -> userService.saveUser(user));

        // Then
        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(userRepository, never()).save(user);

    }

    @Test
    @DisplayName("Test para devolver el token al iniciar sesión")
    void getAuthToken() {
        // Arrange
        User user = new User();
        String username = "testUsername";
        String password = "plainPassword";
        user.setUsername(username);
        user.setPassword(password);
        user.setId(1L);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, password))
                .thenReturn(true);
        when(jwtServices.generateToken(username, 1L))
                .thenReturn("AuthToken");

        //When
        String token = userService.loginUser(username, password);

        //Assert
        assertEquals("AuthToken", token);
    }

    @Test
    @DisplayName("Test para devolver error al iniciar sesión si el usuario no existente o la contraseña es incorrecta")
    void loginuser_Fails_WhenUserDoesNotExistOrPasswordIsIncorrect() {
        // Arrange
        User user = new User();
        String username = "testUsername";
        String usernameNotExist = "nonExistentUser";
        String password = "plainPassword";
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findByUsername(usernameNotExist))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado"));
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, password))
                .thenThrow(new IllegalArgumentException("Contraseña incorrecta"));

        //When
        IllegalArgumentException exceptionUsername = assertThrows(IllegalArgumentException.class, ()
                -> userService.loginUser(usernameNotExist, password));
        IllegalArgumentException exceptionPassword = assertThrows(IllegalArgumentException.class, ()
                -> userService.loginUser(username, password));

        //Assert
        assertEquals("Usuario no encontrado", exceptionUsername.getMessage());
        assertEquals("Contraseña incorrecta", exceptionPassword.getMessage());
    }
}
