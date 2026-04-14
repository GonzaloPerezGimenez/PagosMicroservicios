package com.Paymentshub.Payments_Services.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.Paymentshub.Payments_Services.models.UserPayments;

@FeignClient(name = "user-service", url = "https://redesigned-computing-machine-wjqp6vx9xjqfg9ww-8080.app.github.dev")
public interface UserClient {

    @GetMapping("/users")
    List<UserPayments> getAllUsers();
}
