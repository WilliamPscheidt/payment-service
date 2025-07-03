package br.com.puc.payments.service;

import br.com.puc.payments.dto.PaymentRequestDTO;
import br.com.puc.payments.factory.PaymentProcessor;
import br.com.puc.payments.factory.PaymentProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentProcessorFactory paymentProcessorFactory;

    private PaymentProcessor getPaymentProcessor() {
        return paymentProcessorFactory.createPaymentProcessor("stripe");
    }

    @Override
    public String handleStripeWebhook(String payload, String sigHeader) {
        return getPaymentProcessor().handleWebhook(payload, sigHeader);
    }

    @Override
    public Map<String, String> createSubscription(PaymentRequestDTO paymentRequestDTO, String userId, String email) {
        return getPaymentProcessor().createSubscription(paymentRequestDTO, userId, email);
    }

    @Override
    public Optional<LocalDateTime> getSubscriptionExpirationDate(String email) {
        return getPaymentProcessor().getSubscriptionExpirationDate(email);
    }

    @Override
    public Map<String, String> cancelSubscription(String userId) {
        return getPaymentProcessor().cancelSubscription(userId);
    }
}