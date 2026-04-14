package com.Paymentshub.Payments_Services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserDTO;
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
    public List<UserDTO> getUsersClients() {
        return paymentsService.getAllUsers();
    }
    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id){
        return paymentsService.getUserById(id);
    }
           
    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {
        return paymentsService.createPayment(payment);

    }

}