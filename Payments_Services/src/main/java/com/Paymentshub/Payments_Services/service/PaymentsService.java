package com.Paymentshub.Payments_Services.service;


import java.math.BigDecimal;
import java.util.List;

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

    public UserDTO getUserById(Long id){
        return getExistingUser(id);

    }

    public Payments createPayment(Payments payment) {
        UserDTO sender = getExistingUser(payment.getSendId());
        UserDTO receiver = getExistingUser(payment.getReceiveId());
        validateParticipants(payment.getSendId(), payment.getReceiveId());
        validateSenderBalance(sender.getId(), payment.getAmount());
        doPayment(sender, receiver, payment.getAmount());
        return paymentsRepository.save(payment);

    }

    public List<Payments> getUserPaymentById(Long id) {
        return findPaymentsByUserId(id);
    }

    // Métodos privados para validaciones y lógica de negocio
    private void validateParticipants(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
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

    private void validateSenderBalance(Long senderId, BigDecimal amount) {
        UserDTO sender = getExistingUser(senderId);
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("El usuario con ID " + senderId + " no tiene suficiente saldo para realizar el pago.");
        }    
    }

    private void doPayment(UserDTO sender, UserDTO receiver, BigDecimal amount) {
        userClient.debitUserBalance(sender.getId(), amount);
        userClient.creditUserBalance(receiver.getId(), amount);
    }
    private List <Payments> findPaymentsByUserId(Long id) {
        if(getExistingUser(id)==null) {
            throw new InvalidUserIdException("No se encontró un usuario con ID: " + id);
        }
        List<Payments> payments = getAllPayments().stream()
                .filter(payment -> payment.getSendId().equals(id) || payment.getReceiveId().equals(id))
                .toList();
        return payments; 
    }

}
