package br.com.puc.payments.service.impl;

import br.com.puc.payments.model.Payment;
import br.com.puc.payments.repositories.PaymentRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StripeWebhookServiceImpl implements IStripeWebhookService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Override
    @Transactional
    public String handleStripeWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            System.err.println("Error verifying Stripe webhook signature: " + e.getMessage());
            return "Error: Invalid signature";
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getData().getObject();
            String paymentIdFromStripe = session.getClientReferenceId();

            Optional<Payment> optionalPayment = paymentRepository.findById(Long.valueOf(paymentIdFromStripe));

            if (optionalPayment.isPresent()) {
                Payment payment = optionalPayment.get();

                if (session.getMode().equals("subscription")) {
                    String stripeSubscriptionId = session.getSubscription();
                    payment.setStripeSubscriptionId(stripeSubscriptionId);
                    payment.setStatus("PAID");
                }

                paymentRepository.save(payment);
                String message = "Subscription Completed! User ID (our system): " + payment.getUserId() + ", Subscription ID (Stripe): " + payment.getStripeSubscriptionId() + ". Payload: " + payload;
                System.out.println(message);
                return message;
            } else {
                String message = "Subscription completed, but record not found in database for Stripe Session ID: " + session.getId() + ". Payload: " + payload;
                System.err.println(message);
                return message;
            }
        }
        return "Event received: " + event.getType();
    }
}
