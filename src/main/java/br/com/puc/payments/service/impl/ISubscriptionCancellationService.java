package br.com.puc.payments.service.impl;

import java.util.Map;

public interface ISubscriptionCancellationService {
    Map<String, String> cancelSubscription(String userId);
}
