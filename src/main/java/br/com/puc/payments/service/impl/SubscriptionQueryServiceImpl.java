package br.com.puc.payments.service.impl;

import br.com.puc.payments.model.Payment;
import br.com.puc.payments.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionQueryServiceImpl implements ISubscriptionQueryService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Optional<LocalDateTime> getSubscriptionExpirationDate(String email) {
        List<Payment> payments = paymentRepository.findByEmailAndStatusOrderByCreatedAtDesc(email, "PAID");

        if (payments.isEmpty()) {
            return Optional.empty();
        }

        Payment latestPaidSubscription = payments.get(0);

        if (latestPaidSubscription.getStripeSubscriptionId() == null || latestPaidSubscription.getStripeSubscriptionId().isEmpty()) {
            return Optional.empty();
        }

        try {
            Subscription subscription = Subscription.retrieve(latestPaidSubscription.getStripeSubscriptionId());
            Long currentPeriodEndTimestamp = subscription.getCurrentPeriodEnd();

            if (currentPeriodEndTimestamp != null) {
                LocalDateTime expirationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(currentPeriodEndTimestamp), ZoneId.systemDefault());
                return Optional.of(expirationDate);
            } else {
                return Optional.empty();
            }
        } catch (StripeException e) {
            System.err.println("Error retrieving Stripe subscription for user " + email + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
