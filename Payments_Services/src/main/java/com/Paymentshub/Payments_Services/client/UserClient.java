package com.Paymentshub.Payments_Services.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.Paymentshub.Payments_Services.models.UserPayments;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserClient {

    @GetMapping("/users")
    List<UserPayments> getAllUsers();
}
