package com.Paymentshub.Payments_Services.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Paymentshub.Payments_Services.client.UserClient;
import com.Paymentshub.Payments_Services.exceptions.InvalidUserIdException;
import com.Paymentshub.Payments_Services.exceptions.UserServiceException;
import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserDTO;
import com.Paymentshub.Payments_Services.repository.PaymentsRepository;

import feign.FeignException;

@Service
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final UserClient userClient;

    public PaymentsService(PaymentsRepository paymentsRepository, UserClient userClient) {
        this.paymentsRepository = paymentsRepository;
        this.userClient = userClient;
    }

    public List<Payments> getAllPayments() {
        return paymentsRepository.findAll();
    }

    public List<UserDTO> getAllUsers() {
        try {
            return userClient.getAllUsers();
        } catch (FeignException.FeignServerException ex) {
            throw new UserServiceException("Servicio de usuarios no disponible. Por favor, inténtelo de nuevo más tarde.");
        }
    }

    public UserDTO getUserById(Long id) {
        return getExistingUser(id);

    }

    public ResponseEntity<String> createPayment(Payments payment) {
        doPayment(payment.getSendId(), payment.getReceiveId(), payment.getAmount());
        paymentsRepository.save(payment);
        return ResponseEntity.ok("Pago realizado con éxito."); // Retorna un mensaje de éxito al controlador

    }

    public List<Payments> getPaymentsByUserId(Long userId) {
        return findPaymentsByUserId(userId);
    }

    public ResponseEntity<String> updateUser(Long id, Map<String, String> updates) {
        userClient.updateUser(id, updates);
        return ResponseEntity.ok("Usuario actualizado con éxito."); // Retorna un mensaje de éxito al controlador
    }

    // Métodos privados para validaciones y lógica de negocio
    private void validateParticipants(UserDTO senderUser, UserDTO receiverUser) {
        if (senderUser.getId().equals(receiverUser.getId())) {
            throw new IllegalArgumentException("El remitente y el destinatario no pueden ser el mismo usuario.");
        }

    }

    private UserDTO getExistingUser(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound ex) {
            throw new InvalidUserIdException("No se encontró un usuario con ID: " + userId);
        } catch (FeignException.FeignServerException ex) {
            throw new UserServiceException("Servicio de usuarios no disponible. Por favor, inténtelo de nuevo más tarde.");
        }
    }

    private void validateSenderBalance(UserDTO sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("El usuario con ID " + sender.getId() + " no tiene suficiente saldo para realizar el pago.");
        }
    }

    private void doPayment(Long senderId, Long receiverId, BigDecimal amount) {
        UserDTO senderUser = getExistingUser(senderId);
        UserDTO receiverUser = getExistingUser(receiverId);
        validateParticipants(senderUser, receiverUser);
        validateSenderBalance(senderUser, amount);
        userClient.debitUserBalance(senderId, amount);
        userClient.creditUserBalance(receiverId, amount);
    }

    private List<Payments> findPaymentsByUserId(Long userId) {
        getExistingUser(userId);
        return paymentsRepository.findBysendIdOrReceiveId(userId, userId);
    }

}
