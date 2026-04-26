package com.Paymentshub.Payments_Services.client;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import feign.codec.ErrorDecoder;
 
@Configuration
public class FeignErrorDecoder {
 
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new feign.FeignException.NotFound(
                        "Usuario no encontrado",
                        response.request(),
                        null,
                        null
                );
            }
            return new feign.FeignException.InternalServerError(
                    "Error en el servicio de usuarios",
                    response.request(),
                    null,
                    null
            );
        };
    }
}
 