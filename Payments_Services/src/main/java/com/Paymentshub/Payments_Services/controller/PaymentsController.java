package com.Paymentshub.Payments_Services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserPayments;
import com.Paymentshub.Payments_Services.service.PaymentsService;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    

    @GetMapping
    public List<Payments> getPayments() {
        return paymentsService.getAllPayments();
    }
    @GetMapping("/users")
    public List<UserPayments> getUsersClients() {
        return paymentsService.getAllUsers();
    }
            
    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {
        return paymentsService.createPayment(payment);

    }

}