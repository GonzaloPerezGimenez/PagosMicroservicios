package com.Paymentshub.Payments_Services.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Paymentshub.Payments_Services.models.UserDTO;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    @GetMapping("/users")
    List<UserDTO> getAllUsers();

    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
    
    @PostMapping("/users/{id}/debit")
    UserDTO debitUserBalance(@PathVariable Long id, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/users/{id}/credit")
    UserDTO creditUserBalance(@PathVariable Long id, @RequestParam("amount") BigDecimal amount);
    

}
