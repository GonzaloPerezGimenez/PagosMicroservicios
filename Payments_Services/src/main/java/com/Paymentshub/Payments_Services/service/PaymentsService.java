package com.Paymentshub.Payments_Services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Paymentshub.Payments_Services.client.UserClient;
import com.Paymentshub.Payments_Services.exceptions.InvalidUsernameException;
import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserDTO;
import com.Paymentshub.Payments_Services.repository.PaymentsRepository;

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
        // Validar que el ID no sea nulo o negativo
        return userClient.getUserById(id);
    }

    public Payments createPayment(Payments payment) {
        // Validar que el ID del usuario no sea nulo o negativo
        return paymentsRepository.save(payment);
    }

}
