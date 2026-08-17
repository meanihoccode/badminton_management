package com.example.java_basic.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.example.java_basic.enums.Team;

@Entity
@Table(name = "match_participants")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)
public class MatchParticipant extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private GameMatch match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Team team; // A hoặc B

    @Column(name = "point_difference")
    private Integer pointDifference;

    @Column(name = "fee_calculated")
    private BigDecimal feeCalculated;
}
