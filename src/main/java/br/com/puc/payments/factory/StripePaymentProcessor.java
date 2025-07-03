package br.com.puc.payments.factory;

import br.com.puc.payments.dto.PaymentRequestDTO;
import br.com.puc.payments.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class StripePaymentProcessor implements PaymentProcessor {

    @Autowired
    private ISubscriptionCreationService subscriptionCreationService;

    @Autowired
    private ISubscriptionQueryService subscriptionQueryService;

    @Autowired
    private ISubscriptionCancellationService subscriptionCancellationService;

    @Autowired
    private IStripeWebhookService stripeWebhookService;

    @Override
    public Map<String, String> createSubscription(PaymentRequestDTO paymentRequestDTO, String userId, String email) {
        return subscriptionCreationService.createSubscription(paymentRequestDTO, userId, email);
    }

    @Override
    public Optional<LocalDateTime> getSubscriptionExpirationDate(String email) {
        return subscriptionQueryService.getSubscriptionExpirationDate(email);
    }

    @Override
    public Map<String, String> cancelSubscription(String userId) {
        return subscriptionCancellationService.cancelSubscription(userId);
    }

    @Override
    public String handleWebhook(String payload, String sigHeader) {
        return stripeWebhookService.handleStripeWebhook(payload, sigHeader);
    }
}