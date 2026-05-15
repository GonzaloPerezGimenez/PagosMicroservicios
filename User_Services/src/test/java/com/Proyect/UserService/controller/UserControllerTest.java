package com.Proyect.UserService.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.Proyect.UserService.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Test para verificar el funcionamiento del controlador de usuarios")
    void testCreateUser_UserNameIsEmpty_ReturnsBadRequest() throws Exception {
        // Arrange
        String userJson = """
        {   "nombre": "Test User",
            "username": " ",
            "password": "testpassword"
        }
        """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(any());
    }

    @Test
    @DisplayName("Test para verificar el funcionamiento del controlador de usuarios")
    void testCreateUser_PasswordIsEmpty_ReturnsBadRequest() throws Exception {
        // Arrange
        String userJson = """
        {   "nombre": "Test User",
            "username": "testuser",
            "password": " "

        }
        """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(any());
    }

}
