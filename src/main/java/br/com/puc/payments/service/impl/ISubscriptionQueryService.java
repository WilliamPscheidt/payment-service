package br.com.puc.payments.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ISubscriptionQueryService {
    Optional<LocalDateTime> getSubscriptionExpirationDate(String email);
}
