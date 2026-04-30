package com.Paymentshub.Payments_Services.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserDTO;
import com.Paymentshub.Payments_Services.service.PaymentsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private final PaymentsService paymentsService; 

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    public Long getAuthenticatedUserId(Long loginUserId, String userId) {
        if (!loginUserId.equals(Long.valueOf(userId))) {
            throw new IllegalArgumentException("ID de usuario no autorizado");
        }
        return loginUserId;
    }

    @GetMapping
    public List<Payments> getPayments() {
        return paymentsService.getAllPayments();
    }

    @GetMapping("/{id}")
    public List<Payments> getUserPayments(@PathVariable Long id) {
        return paymentsService.getPaymentsByUserId(id);
    }

    @GetMapping("/users")
    public List<UserDTO> getUsersClients() {
        return paymentsService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id, @RequestHeader("X-User-Id") String userId) {
        return paymentsService.getUserById(getAuthenticatedUserId(id, userId));
    }

    @PutMapping("/users/{id}/update")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        return paymentsService.updateUser(id, updates);
    }

    @PostMapping
    public ResponseEntity<String> createPayment(@Valid @RequestBody Payments payment) {
        return paymentsService.createPayment(payment);
    }

}
