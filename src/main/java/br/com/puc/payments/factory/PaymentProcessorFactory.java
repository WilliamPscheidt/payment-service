package br.com.puc.payments.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessorFactory {

    @Autowired
    private StripePaymentProcessor stripePaymentProcessor;

    public PaymentProcessor createPaymentProcessor(String type) {
        return switch (type.toLowerCase()) {
            case "stripe" -> stripePaymentProcessor;
            default -> throw new IllegalArgumentException("Unsupported payment processor: " + type);
        };
    }
}