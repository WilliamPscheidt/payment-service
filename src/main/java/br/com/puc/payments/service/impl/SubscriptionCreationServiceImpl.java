package br.com.puc.payments.service.impl;

import br.com.puc.payments.dto.PaymentRequestDTO;
import br.com.puc.payments.model.Payment;
import br.com.puc.payments.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SubscriptionCreationServiceImpl implements ISubscriptionCreationService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionQueryService subscriptionQueryService;

    private static final Map<String, String> PLAN_PRICE_IDS = new HashMap<>();

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        PLAN_PRICE_IDS.put("Monthly", "price_1Rfl5RP12g0KVo8hNxeuNyM4");
        PLAN_PRICE_IDS.put("Semiannual", "price_1Rfl5zP12g0KVo8hFIWHaVxi");
        PLAN_PRICE_IDS.put("Annual", "price_1Rfl6DP12g0KVo8hmqrJ0bIO");
    }

    @Override
    @Transactional
    public Map<String, String> createSubscription(PaymentRequestDTO paymentRequestDTO, String userId, String email) {
        try {
            String priceId = PLAN_PRICE_IDS.get(paymentRequestDTO.getPlanType());
            if (priceId == null) {
                throw new IllegalArgumentException("Invalid plan type: " + paymentRequestDTO.getPlanType() + ". Valid types: Monthly, Semiannual, Annual.");
            }

            Optional<LocalDateTime> expirationDate = subscriptionQueryService.getSubscriptionExpirationDate(userId);
            if (expirationDate.isPresent() && expirationDate.get().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("User already has an active subscription expiring on: " + expirationDate.get());
            }

            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setEmail(email);
            payment.setPlanType(paymentRequestDTO.getPlanType());
            payment.setStatus("PENDING");
            payment = paymentRepository.save(payment);

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(paymentRequestDTO.getPlanType() + " Subscription")
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("brl")
                            .setUnitAmount(0L)
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setPrice(priceId)
                            .setQuantity(paymentRequestDTO.getQuantity() != null ? paymentRequestDTO.getQuantity() : 1L)
                            .build();

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setClientReferenceId(String.valueOf(payment.getId()))
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl("http://localhost:3000/cancel")
                            .addLineItem(lineItem)
                            .setCustomerEmail(email)
                            .build();

            Session session = Session.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Error creating Stripe subscription session: " + e.getMessage(), e);
        }
    }
}
