package com.Proyect.UserService.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.Proyect.UserService.model.UserResponseDTO;
import com.Proyect.UserService.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Test para verificar que se crea un usuario correctamente con datos válidos")
    void testCreateUser_ValidData_ReturnsOk() throws Exception {
        // Arrange
        String userJson = """
        {   "nombre": "Test User",
            "username": "testuser",
            "password": "testpassword"
        }
        """;
        when(userService.saveUser(any()))
                .thenReturn(ResponseEntity.ok("Usuario registrado con éxito."));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado con éxito."));

        verify(userService).saveUser(any());
    }

    @Test
    @DisplayName("Test para verificar que se retorna un error al crear un usuario con username vacío o sin el campo username")
    void testCreateUser_UserNameIsEmpty_ReturnsBadRequest() throws Exception {
        // Arrange
        String userJson = """
        {   "nombre": "Test User",
            "username": " ",
            "password": "testpassword"
        }
        """;
        String userJsonNotUsername = """
        {   "nombre": "Test User",
            "password": "testpassword"
        }
        """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJsonNotUsername))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
        verify(userService, never()).saveUser(any());
    }

    @Test
    @DisplayName("Test para verificar que se retorna un error al crear un usuario con contraseña vacía")
    void testCreateUser_PasswordIsEmpty_ReturnsBadRequest() throws Exception {
        // Arrange
        String userJsonNoPassword = """
        {   "nombre": "Test User",
            "username": "testuser",
            "password": " "

        }
        """;
        String userJsonShortPass = """
        {   "nombre": "Test User",
            "username": "testuser",
            "password": "123" 

        }
        """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJsonNoPassword))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJsonShortPass))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        verify(userService, never()).saveUser(any());
    }

    @Test
    @DisplayName("Test para verificar que se retorna un token al iniciar sesión con credenciales válidas")
    void testLoginReturnToken() throws Exception {
        // Arrange
        String userJson = """
        {   
            "username": "testuser",
            "password": "testpassword"
        }
        """;
        when(userService.loginUser("testuser", "testpassword"))
                .thenReturn("token");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("token"));

        verify(userService).loginUser(any(), any());
    }

    @Test
    @DisplayName("Test para verificar que se retorna la lista de los usuarios.")
    void testGetAllUsers() throws Exception {
        // Arrange
        UserResponseDTO user1 = new UserResponseDTO(Long.valueOf(1), "Test User 1", "testuser1", BigDecimal.valueOf(0));
        UserResponseDTO user2 = new UserResponseDTO(Long.valueOf(2), "Test User 2", "testuser2", BigDecimal.valueOf(0));
        List<UserResponseDTO> allUsers = List.of(user1, user2);
        when(userService.getAllUsers())
                .thenReturn(allUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("testuser2"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Test para verificar que se retorna el usuario por ID.")
    void testGetUserById() throws Exception {
        // Arrange
        UserResponseDTO userDTO = new UserResponseDTO(Long.valueOf(1), "Test User 1", "testuser1", BigDecimal.valueOf(0));
        when(userService.getUserById(1L))
                .thenReturn(userDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser1"))
                .andExpect(jsonPath("$.balance").value(0));

        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("Test para verificar que se actualiza el usuario por ID.")
    void testUpdateUserById() throws Exception {
        // Arrange
        String updatesJson = """
        {
            "nombre": "Updated User",
            "username": "updateduser"
        }
        """;
        when(userService.updateUser(any(), any()))
                .thenReturn(ResponseEntity.ok("Usuario actualizado con éxito."));

        mockMvc.perform(put("/users/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatesJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario actualizado con éxito."));

        verify(userService).updateUser(any(), any());
    }

    @Test
    @DisplayName("Test para verificar que se elimina el usuario por ID.")
    void testDeleteUserById() throws Exception {
        // Arrange
        when(userService.deleteUser(1L))
                .thenReturn(ResponseEntity.ok("Usuario eliminado"));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado"));

        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

}
