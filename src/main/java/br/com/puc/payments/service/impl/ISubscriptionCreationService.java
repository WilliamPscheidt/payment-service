package br.com.puc.payments.service.impl;

import br.com.puc.payments.dto.PaymentRequestDTO;
import java.util.Map;

public interface ISubscriptionCreationService {
    Map<String, String> createSubscription(PaymentRequestDTO paymentRequestDTO, String userId, String email);
}
