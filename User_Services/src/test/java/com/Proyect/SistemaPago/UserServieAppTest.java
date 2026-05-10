package com.Proyect.SistemaPago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Proyect.UserService.model.User;
import com.Proyect.UserService.repository.UserRepository;
import com.Proyect.UserService.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServieAppTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
}
