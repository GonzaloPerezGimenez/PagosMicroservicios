package com.Paymentshub.Payments_Services.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;
    @NotNull(message = "El ID del remitente no puede ser nulo")
    private Long sendId;
    @NotNull(message = "El ID del destinatario no puede ser nulo")
    private Long receiveId;
    private LocalDate date;

    public Payments(BigDecimal amount, Long sendId, Long receiveId) {
        this.amount = amount;
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.date = LocalDate.now();
    }

    public Payments() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getSendId() {
        return sendId;
    }

    public void setSendId(Long sendId) {
        this.sendId = sendId;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
