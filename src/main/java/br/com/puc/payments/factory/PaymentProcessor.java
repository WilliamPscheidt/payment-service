package br.com.puc.payments.factory;

import br.com.puc.payments.dto.PaymentRequestDTO;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface PaymentProcessor {
    Map<String, String> createSubscription(PaymentRequestDTO paymentRequestDTO, String userId, String email);
    Optional<LocalDateTime> getSubscriptionExpirationDate(String email);
    Map<String, String> cancelSubscription(String userId);
    String handleWebhook(String payload, String sigHeader);
}