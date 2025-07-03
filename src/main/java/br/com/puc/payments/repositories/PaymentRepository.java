package br.com.puc.payments.repositories;

import br.com.puc.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByUserId(String userId);

    List<Payment> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);

    List<Payment> findByEmailAndStatusOrderByCreatedAtDesc(String email, String status);
}
