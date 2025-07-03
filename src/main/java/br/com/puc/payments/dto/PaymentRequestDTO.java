package br.com.puc.payments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    @NotBlank(message = "Plan type is mandatory")
    private String planType;

    private Long quantity;
}