package br.com.puc.payments.service.impl;

import br.com.puc.payments.model.Payment;
import br.com.puc.payments.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionCancellationServiceImpl implements ISubscriptionCancellationService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Map<String, String> cancelSubscription(String userId) {
        Map<String, String> response = new HashMap<>();
        List<Payment> payments = paymentRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "PAID");

        if (payments.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No active subscription found for user: " + userId);
            return response;
        }

        Payment latestPaidSubscription = payments.get(0);

        if (latestPaidSubscription.getStripeSubscriptionId() == null || latestPaidSubscription.getStripeSubscriptionId().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Stripe subscription ID not found for the latest subscription of user: " + userId);
            return response;
        }

        try {
            Subscription subscription = Subscription.retrieve(latestPaidSubscription.getStripeSubscriptionId());
            Subscription canceledSubscription = subscription.cancel();

            latestPaidSubscription.setStatus("CANCELED");
            paymentRepository.save(latestPaidSubscription);

            response.put("subscription", canceledSubscription.getId());
            response.put("userId", userId);
            response.put("status", "canceled");
            return response;
        } catch (StripeException e) {
            throw new RuntimeException("Error canceling Stripe subscription for user " + userId + ": " + e.getMessage(), e);
        }
    }
}
