package br.com.puc.payments.service.impl;

public interface IStripeWebhookService {
    String handleStripeWebhook(String payload, String sigHeader);
}
