package br.com.puc.payments.controllers;

import br.com.puc.payments.dto.PaymentRequestDTO;
import br.com.puc.payments.dto.SubscriptionExpirationRequestDTO;
import br.com.puc.payments.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PreAuthorize("hasAnyRole({'admin', 'instructor', 'student'})")
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> createSubscription(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        Map<String, String> response = paymentService.createSubscription(paymentRequestDTO, userId, email);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole({'admin', 'instructor', 'student'})")
    @PostMapping("/subscription/expiration")
    public ResponseEntity<Map<String, Object>> getSubscriptionExpirationDate(@Valid @RequestBody SubscriptionExpirationRequestDTO requestDTO) {
        Optional<LocalDateTime> expirationDateOptional = paymentService.getSubscriptionExpirationDate(requestDTO.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (expirationDateOptional.isPresent()) {
            LocalDateTime expirationDate = expirationDateOptional.get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            response.put("expire", expirationDate.format(formatter));
            response.put("active", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("expire", null);
            response.put("active", false);
            return ResponseEntity.ok(response);
        }
    }

    @PreAuthorize("hasAnyRole({'admin', 'instructor', 'student'})")
    @DeleteMapping("/subscription")
    public ResponseEntity<Map<String, String>> cancelSubscription(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Map<String, String> response = paymentService.cancelSubscription(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook/confirmPayment")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        String message = paymentService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok(message);
    }
}