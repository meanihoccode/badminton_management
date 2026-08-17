package com.example.java_basic.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.java_basic.enums.TransactionType;

@Entity
@Table(name = "transactions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)
public class Transaction extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // MATCH_FEE, DEPOSIT

    private String description;
}