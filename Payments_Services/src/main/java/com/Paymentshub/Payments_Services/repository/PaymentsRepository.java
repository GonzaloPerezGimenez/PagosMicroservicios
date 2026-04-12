package com.Paymentshub.Payments_Services.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Paymentshub.Payments_Services.models.Payments;


@Repository
public interface PaymentsRepository  extends JpaRepository<Payments, Long> {


}
