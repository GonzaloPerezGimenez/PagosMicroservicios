package com.Paymentshub.Payments_Services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Paymentshub.Payments_Services.client.UserClient;
import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserPayments;
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
    
    public List<UserPayments> getAllUsers() {
        return userClient.getAllUsers();
    }

    public Payments createPayment(Payments payment) {
        payment.setStatus("PENDING"); // Set default status
        paymentsRepository.save(payment);
        return paymentsRepository.findById(payment.getId()).orElse(null);
    }

}
