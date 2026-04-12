package com.Paymentshub.Payments_Services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class PaymentsServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsServicesApplication.class, args);
	}

}
