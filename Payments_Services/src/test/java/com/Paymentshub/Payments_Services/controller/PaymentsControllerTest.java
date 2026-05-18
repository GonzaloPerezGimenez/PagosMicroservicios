package com.Paymentshub.Payments_Services.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.Paymentshub.Payments_Services.models.Payments;
import com.Paymentshub.Payments_Services.models.UserDTO;
import com.Paymentshub.Payments_Services.service.PaymentsService;

@WebMvcTest(PaymentsController.class)
public class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PaymentsService paymentsService;

    @Test
    @DisplayName("Test para crear un pago correcto")
    void createPayment() throws Exception {
        //Arranque
        String paymentJson = """
        {   "amount":100,
            "sendId":1,
            "receiveId":2
        }
        """;
        when(paymentsService.createPayment(any(Payments.class)))
                .thenReturn(ResponseEntity.ok("Pago realizado con éxito."));

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Pago realizado con éxito."));

        verify(paymentsService, times(1)).createPayment(any());
    }

    @Test
    @DisplayName("Test para validar una excepcion de un pago con pago negativo")
    void validateExceptionPaymentNegative() throws Exception {
        //Arranque
        String paymentJson = """
        {   "amount":-100,
            "sendId":1,
            "receiveId":2
        }
        """;

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andExpect(status().isBadRequest());

        verify(paymentsService, never()).createPayment(any());
    }

    @Test
    @DisplayName("Test para validar una excepcion de un pago sin remitente o destinatario")
    void validateExceptionPaymentWithoutSender() throws Exception {
        //Arranque
        String paymentJson = """
        {   "amount":100,
            "sendId":null,
            "receiveId":2
        }
        """;
        String paymentJson2 = """
        {   "amount":100,
            "sendId":1,
            "receiveId":null
        }
        """;

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson2))
                .andExpect(status().isBadRequest());

        verify(paymentsService, never()).createPayment(any());
    }

    @Test
    @DisplayName("Test para consultar los pagos de un usuario con ID autorizado")
    void getPaymentsByUserId() throws Exception {
        //Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        Payments payment1 = new Payments(new BigDecimal("100.00"), 1L, 2L);
        payment1.setId(1L);
        List<Payments> payments = List.of(payment1);

        when(paymentsService.getPaymentsByUserId(userDTOSender.getId()))
                .thenReturn(payments);

        mockMvc.perform(get("/payments/1")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sendId").value(1))
                .andExpect(jsonPath("$[0].receiveId").value(2))
                .andExpect(jsonPath("$[0].amount").value(100.00));

        verify(paymentsService, times(1)).getPaymentsByUserId(1L);
    }

    @Test
    @DisplayName("Test para realizar un deposito con ID autorizado")
    void depositUserBalance() throws Exception {
        //Arranque
        UserDTO userDTOSender = new UserDTO("Test Sender", "testsender");
        userDTOSender.setId(1L);
        String depositJson = """
        {   "amount":100  }
        """;

        when(paymentsService.depositUserBalance(userDTOSender.getId(), new BigDecimal("100.00")))
                .thenReturn(ResponseEntity.ok("Se han depositado los fondos con éxito."));

        mockMvc.perform(post("/payments/users/1/deposit")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(depositJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Se han depositado los fondos con éxito."));

        verify(paymentsService).depositUserBalance(1L, new BigDecimal(100.00));

    }

}
