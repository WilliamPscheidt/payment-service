package br.com.puc.payments.service;

import br.com.puc.payments.dto.PaymentRequestDTO;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {

    String handleStripeWebhook(String payload, String sigHeader);

    Map<String, String> createSubscription(PaymentRequestDTO paymentRequestDTO, String userId, String email);

    Optional<LocalDateTime> getSubscriptionExpirationDate(String email);

    Map<String, String> cancelSubscription(String userId);
}