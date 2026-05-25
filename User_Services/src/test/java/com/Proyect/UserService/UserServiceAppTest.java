package com.Proyect.UserService;

import java.math.BigDecimal;
import java.util.Map;
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
import org.springframework.web.server.ResponseStatusException;

import com.Proyect.UserService.config.JwtServices;
import com.Proyect.UserService.exceptions.UsernameAlreadyExist;
import com.Proyect.UserService.model.User;
import com.Proyect.UserService.model.UserResponseDTO;
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
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        when(passwordEncoder.encode(user.getPassword()))
                .thenReturn("encryptedPassword");
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        // When
        userService.saveUser(user);

        // Then
        assertEquals("encryptedPassword", user.getPassword());
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("Test_Pass");
        verify(userRepository).findByUsername("testUsername");

    }

    @Test
    @DisplayName("Test para registrar un usuario")
    void saveUser() {
        // Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());
        when(userRepository.save(user))
                .thenReturn(user);

        // When
        ResponseEntity<String> result = userService.saveUser(user);

        // Then
        assertEquals(ResponseEntity.ok("Usuario registrado con éxito."), result);
        verify(userRepository).findByUsername("testUsername");
        verify(userRepository).save(user);

    }

    @Test
    @DisplayName("Test para validar que falla al registrar un usuario con username ya existente")
    void validateUserRegistration_Fails_WhenUsernameAlreadyExists() {
        // Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");

        when(userRepository.findByUsername("testUsername"))
                .thenReturn(Optional.of(user));

        // When
        UsernameAlreadyExist exception = assertThrows(UsernameAlreadyExist.class, () -> userService.saveUser(user));

        // Then
        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(userRepository, never()).save(user);
        verify(userRepository).findByUsername("testUsername");
        verify(passwordEncoder, never()).encode(any(String.class));

    }

    @Test
    @DisplayName("Test para devolver el token al iniciar sesión")
    void getAuthToken() {
        // Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtServices.generateToken(user.getUsername(), 1L))
                .thenReturn("AuthToken");

        //When
        String token = userService.loginUser(user.getUsername(), user.getPassword());

        //Assert
        assertEquals("AuthToken", token);
    }

    @Test
    @DisplayName("Test para devolver error al iniciar sesión si el usuario no existente")
    void loginuser_Fails_WhenUserDoesNotExist() {
        // Arrange
        String usernameNotExist = "nonExistentUser";

        when(userRepository.findByUsername(usernameNotExist))
                .thenReturn(Optional.empty());

        //When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> userService.loginUser(usernameNotExist, "anyPassword"));
        //Assert
        assertEquals("Usuario " + usernameNotExist + " no encontrado", exception.getReason());
        verify(userRepository).findByUsername(usernameNotExist);
        verify(passwordEncoder, never()).matches(any(String.class), any(String.class));
        verify(jwtServices, never()).generateToken(any(String.class), any(Long.class));
    }

    @Test
    @DisplayName("Test para devolver error al iniciar sesión si la contraseña es incorrecta")
    void loginuser_Fails_WhenPasswordIsIncorrect() {
        // Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("PasswordIncorrecta", user.getPassword()))
                .thenReturn(false);
        //When
        RuntimeException exceptionPassword = assertThrows(RuntimeException.class, ()
                -> userService.loginUser(user.getUsername(), "PasswordIncorrecta"));
        //Assert
        assertEquals("Contraseña incorrecta", exceptionPassword.getMessage());
        verify(userRepository).findByUsername(user.getUsername());
        verify(passwordEncoder).matches("PasswordIncorrecta", user.getPassword());
        verify(jwtServices, never()).generateToken(any(String.class), any(Long.class));
    }

    @Test
    @DisplayName("Test que devuelve un usuario al buscar por ID")
    void getExistingUserById() {
        //Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //When
        UserResponseDTO userResponse = userService.getUserById(1L);

        //Then
        assertEquals("testUsername", userResponse.getUsername());
        assertEquals(1L, userResponse.getId());
        verify(userRepository).findById(1L);

    }

    @Test
    @DisplayName("Test que devuelve error al buscar un usuario por username que no existe")
    void validateToFindUserById_WhenUserDontExist() {
        //Arrange
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        //When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()
                -> userService.getUserById(1L));

        //Then
        assertEquals("Usuario con ID " + 1 + " no encontrado", exception.getReason());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Test que suma el monto al balance del usuario")
    void creditUserBalance() {
        //Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        user.setBalance(BigDecimal.valueOf(100));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        //When
        ResponseEntity<String> respuesta = userService.creditUserBalance(1L, BigDecimal.valueOf(100));

        //Then
        assertEquals("Cobro realizado con éxito.", respuesta.getBody());
        assertEquals(BigDecimal.valueOf(200), user.getBalance());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Test que resta el monto al balance del usuario")
    void debitUserBalance() {
        //Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        user.setBalance(BigDecimal.valueOf(100));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        //When
        ResponseEntity<String> respuesta = userService.debitUserBalance(1L, BigDecimal.valueOf(100));

        //Then
        assertEquals("Pago realizado con éxito.", respuesta.getBody());
        assertEquals(BigDecimal.valueOf(0), user.getBalance());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Test que da error cuando resta el monto al balance del usuario y no hay saldo suficiente")
    void validateToDebitUserBalance_WhenInsufficientBalance() {
        //Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        user.setBalance(BigDecimal.valueOf(10));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        //When
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, ()
                -> userService.debitUserBalance(1L, BigDecimal.valueOf(100)));

        //Then
        assertEquals("Saldo insuficiente para realizar el débito.", excepcion.getMessage());
        assertEquals(BigDecimal.valueOf(10), user.getBalance());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("Test que actualiza la contraseña del usuario")
    void updateUserPassword() {
        //Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode("New_Pass"))
                .thenReturn("Encoded_New_Pass");
        //When
        ResponseEntity<String> respuesta = userService.updateUser(1L, Map.of("password", "New_Pass"));
        //Then
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("New_Pass");
        verify(userRepository).save(user);
        assertEquals("Usuario actualizado con éxito.", respuesta.getBody());
        assertEquals("Encoded_New_Pass", user.getPassword());
    }

    @Test
    @DisplayName("Test que elimina un usuario")
    void deleteUser() {
        //Arrange
        User user = createUser(1L, "test_Name", "testUsername", "Test_Pass");
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        //When
        ResponseEntity<String> respuesta = userService.deleteUser(1L);
        //Then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
        assertEquals("Usuario eliminado con éxito.", respuesta.getBody());
    }

    @Test
    @DisplayName("Test que da error al eliminar un usuario que no existe")
    void validateToDeleteUser_WhenUserDoesNotExist() {
        //Arrange
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        //When
        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class, ()
                -> userService.deleteUser(1L));

        //Then
        assertEquals("Usuario con ID 1 no encontrado", excepcion.getReason());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    //Creacion de usuarios
    private User createUser(Long id, String nombre, String username, String password) {
        User user = new User(nombre, username, password);
        user.setId(id);
        return user;
    }
}
