package com.Paymentshub.Payments_Services.service;


import java.math.BigDecimal;
import java.util.List;


import org.springframework.stereotype.Service;

import com.Paymentshub.Payments_Services.client.UserClient;
import com.Paymentshub.Payments_Services.exceptions.InvalidUserIdException;
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
        return userClient.getAllUsers();
    }

    public UserDTO getUserById(Long id){
        validateUserId(id);
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


    private void validateParticipants(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("El remitente y el destinatario no pueden ser el mismo usuario.");
        }

    }
    private UserDTO getExistingUser(Long userId) {
        try {
            validateUserId(userId);
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound ex) {
            throw new InvalidUserIdException("No se encontró un usuario con ID: " + userId);
        }
    }

    private void validateSenderBalance(Long senderId, BigDecimal amount) {
        UserDTO sender = getExistingUser(senderId);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("El usuario con ID " + senderId + " no tiene suficiente saldo para realizar el pago.");
        }
        
    }
     private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new InvalidUserIdException("El ID del usuario no puede ser nulo o negativo");
        }
    }

    private void doPayment(UserDTO sender, UserDTO receiver, BigDecimal amount) {
        userClient.updateUserBalance(sender.getId(), sender.getBalance().subtract(amount));
        userClient.updateUserBalance(receiver.getId(), receiver.getBalance().add(amount));
    }

}
