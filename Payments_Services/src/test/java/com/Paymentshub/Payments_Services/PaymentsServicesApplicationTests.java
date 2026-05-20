package com.Paymentshub.Payments_Services;

import java.math.BigDecimal;
import java.util.List;

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

import com.Paymentshub.Payments_Services.client.UserClient;
import com.Paymentshub.Payments_Services.exceptions.InvalidUserIdException;
import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserDTO;
import com.Paymentshub.Payments_Services.repository.PaymentsRepository;
import com.Paymentshub.Payments_Services.service.PaymentsService;

@ExtendWith(MockitoExtension.class)
class PaymentsServicesApplicationTests {

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    UserClient userClient;

    @InjectMocks
    private PaymentsService paymentsService;

    @Test
    @DisplayName("Test para verificar que lanza excepción al intentar obtener un usuario con ID no existente")
    void getUserById_UserNotFound_ThrowsException() {
        // Arranque
        Long nonExistentUserId = 999L;
        when(userClient.getUserById(nonExistentUserId))
                .thenThrow(new RuntimeException("No se encontró un usuario con ID: " + nonExistentUserId));
        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentsService.getUserById(nonExistentUserId));

        // Then
        assertEquals("No se encontró un usuario con ID: " + nonExistentUserId, exception.getMessage());
    }

    @Test
    @DisplayName("Test para verificar que el servicio de pagos crea un pago correctamente")
    void createPaymentTest() {
        // Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        userDTOSender.setBalance(BigDecimal.valueOf(200.00));

        UserDTO userDTODestination = new UserDTO("Test Destination", "testdestination");
        userDTODestination.setId(2L);
        Payments payment = new Payments(BigDecimal.valueOf(100.00), userDTOSender.getId(), userDTODestination.getId());

        when(userClient.getUserById(userDTOSender.getId()))
                .thenReturn(userDTOSender);
        when(userClient.getUserById(userDTODestination.getId()))
                .thenReturn(userDTODestination);
        when(paymentsRepository.save(payment))
                .thenReturn(payment);

        // When
        ResponseEntity<String> response = paymentsService.createPayment(payment);

        // Then
        verify(paymentsRepository).save(payment);
        assertEquals(ResponseEntity.ok("Pago realizado con éxito."), response);

    }

    @Test
    @DisplayName("Test para verificar que el servicio de pagos resta el saldo del remitente y suma el saldo al destinatario correctamente")
    void createPaymentTestWithBalanceUpdate() {
        // Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        userDTOSender.setBalance(BigDecimal.valueOf(200.00));

        UserDTO userDTODestination = new UserDTO("Test Destination", "testdestination");
        userDTODestination.setId(2L);
        Payments payment = new Payments(BigDecimal.valueOf(100.00), userDTOSender.getId(), userDTODestination.getId());

        when(userClient.getUserById(userDTOSender.getId()))
                .thenReturn(userDTOSender);
        when(userClient.getUserById(userDTODestination.getId()))
                .thenReturn(userDTODestination);
        when(paymentsRepository.save(payment))
                .thenReturn(payment);

        // When
        paymentsService.createPayment(payment);

        // Then
        verify(userClient).debitUserBalance(userDTOSender.getId(), payment.getAmount());
        verify(userClient).creditUserBalance(userDTODestination.getId(), payment.getAmount());
    }

    @Test
    @DisplayName("Test para lanzar excepcion al intentar realizar un pago al mismo usuario")
    void validateExceptionWhenPayingToSameUser() {
        //Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        userDTOSender.setBalance(BigDecimal.valueOf(200.00));
        Payments payment = new Payments(BigDecimal.valueOf(100.00), userDTOSender.getId(), userDTOSender.getId());

        when(userClient.getUserById(userDTOSender.getId()))
                .thenReturn(userDTOSender);

        //When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(payment));

        //Then
        assertEquals("El remitente y el destinatario no pueden ser el mismo usuario.", exception.getMessage());

    }

    @Test
    @DisplayName("Test para lanzar excepcion al intentar realizar un pago con saldo insuficiente")
    void validateExceptionWhenPayingWithoutSufficientBalance() {
        //Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        userDTOSender.setBalance(BigDecimal.valueOf(99.99));
        UserDTO userDTODestination = new UserDTO("Test Destination", "testdestination");
        userDTODestination.setId(2L);
        Payments payment = new Payments(BigDecimal.valueOf(100.00), userDTOSender.getId(), userDTODestination.getId());

        when(userClient.getUserById(userDTOSender.getId()))
                .thenReturn(userDTOSender);
        when(userClient.getUserById(userDTODestination.getId()))
                .thenReturn(userDTODestination);

        //When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentsService.createPayment(payment));

        //Then
        assertEquals("No tiene suficiente saldo", exception.getMessage());
        verify(paymentsRepository, never()).save(any(Payments.class));
    }

    @Test
    @DisplayName("Test para lanzar excepcion al enviar un pago a un destinatario inexistente")
    void validateExceptionWhenPayingToNonExistentUser() {
        // Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        userDTOSender.setBalance(BigDecimal.valueOf(150.00));

        Payments payment = new Payments(BigDecimal.valueOf(100.00), userDTOSender.getId(), 2L);

        when(userClient.getUserById(userDTOSender.getId()))
                .thenReturn(userDTOSender);
        when(userClient.getUserById(2L))
                .thenThrow(new InvalidUserIdException("El ID del destinatario no es válido"));

        // When
        InvalidUserIdException exception = assertThrows(InvalidUserIdException.class, () -> paymentsService.createPayment(payment));
        // Then
        assertEquals("El ID del destinatario no es válido", exception.getMessage());
    }

    @Test
    @DisplayName("Test para verificar que se obtiene los pagos de un usuario correctamente")
    void getPaymentsByUserIdTest() {
        //Arranque
        Payments payment1 = new Payments(BigDecimal.valueOf(100.00), 1L, 2L);
        payment1.setId(1L);
        List<Payments> payments = List.of(payment1);
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);

        when(userClient.getUserById(userDTOSender.getId()))
                .thenReturn(userDTOSender);
        when(paymentsRepository.findBysendIdOrReceiveId(userDTOSender.getId(), userDTOSender.getId()))
                .thenReturn(payments);
        //When
        List<Payments> listPayment = paymentsService.getPaymentsByUserId(1L);

        //Then
        verify(paymentsRepository).findBysendIdOrReceiveId(1L, 1L);
        verify(userClient).getUserById(1L);
        assertEquals(payments, listPayment);
    }

    @Test
    @DisplayName("Test para verificar que hay excepcion al no existir el usuario")
    void getExcepcionIfUserIdDontExist() {
        //Arranque
        Long userID = 1L;
        when(userClient.getUserById(userID))
                .thenThrow(new InvalidUserIdException("El usuario con ID 1 no existe"));

        //When
        InvalidUserIdException exception = assertThrows(InvalidUserIdException.class, ()
                -> paymentsService.getPaymentsByUserId(userID));

        //Then
        verify(paymentsRepository, never()).findBysendIdOrReceiveId(any(), any());
        assertEquals("El usuario con ID 1 no existe", exception.getMessage());
    }

    @Test
    @DisplayName("Test para verificar que devuelve todos los pagos")
    void validateReturnedAllPayments() {
        //Arranque
        Payments payment1 = new Payments(BigDecimal.valueOf(100.00), 1L, 2L);
        payment1.setId(1L);
        List<Payments> payments = List.of(payment1);
        when(paymentsRepository.findAll())
                .thenReturn(payments);
        //When
        List<Payments> listOfPayments = paymentsService.getAllPayments();

        //Then
        verify(paymentsRepository).findAll();
        assertEquals(payments, listOfPayments);
        assertEquals(1, listOfPayments.size());
        assertEquals(1L, listOfPayments.get(0).getId());
        assertEquals(1L, listOfPayments.get(0).getSendId());
        assertEquals(2L, listOfPayments.get(0).getReceiveId());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(listOfPayments.get(0).getAmount()));
    }

    @Test
    @DisplayName("Test para verificar que devuelve todos los usuarios")
    void validateReturnedAllUsers() {
        //Arranque
        UserDTO user1 = new UserDTO("Test User 1", "testuser1");
        user1.setId(1L);
        List<UserDTO> users = List.of(user1);
        when(userClient.getAllUsers())
                .thenReturn(users);
        //When
        List<UserDTO> listOfUsers = paymentsService.getAllUsers();

        //Then
        verify(userClient).getAllUsers();
        assertEquals(users, listOfUsers);
        assertEquals(1, listOfUsers.size());
        assertEquals(1L, listOfUsers.get(0).getId());
        assertEquals("testuser1", listOfUsers.get(0).getUsername());
    }
}
