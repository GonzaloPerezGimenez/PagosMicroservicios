package com.Paymentshub.Payments_Services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Paymentshub.Payments_Services.client.UserClient;
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
        return userClient.getUserById(id);
    }

    public Payments createPayment(Payments payment) {
        Long id = payment.getUserId();
        if (userClient.getUserById(id)== null) {
            throw new IllegalArgumentException("El usuario con ID " + id + " no existe");
        }
        paymentsRepository.save(payment);
        return paymentsRepository.findById(payment.getId()).orElse(null);
    }

}
