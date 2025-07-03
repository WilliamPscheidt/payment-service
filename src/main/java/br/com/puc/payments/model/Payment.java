package br.com.puc.payments.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String email;

    private String planType;

    private String stripeSubscriptionId;

    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
