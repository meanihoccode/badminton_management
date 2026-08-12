package com.example.java_basic.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "badminton_sessions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)
public class BadmintonSession extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Column(name = "total_court_fee")
    private BigDecimal totalCourtFee;

    @Column(name = "shuttlecock_fee")
    private BigDecimal shuttlecockFee;

    @Column(nullable = false)
    private String status; // OPEN, COMPLETED

    // 1 Buổi đánh có nhiều Trận đấu
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<GameMatch> matches;
}
