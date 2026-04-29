package com.Paymentshub.Payments_Services.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Paymentshub.Payments_Services.models.Payments;


@Repository
public interface PaymentsRepository  extends JpaRepository<Payments, Long> {
    List<Payments> findBysendIdOrReceiveId(Long sendId, Long receiveId);

}
