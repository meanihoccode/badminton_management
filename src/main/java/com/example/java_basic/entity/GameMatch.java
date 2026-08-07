package com.example.java_basic.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "game_matches")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GameMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private BadmintonSession session;

    @Column(name = "team_a_score", nullable = false)
    private Integer teamAScore;

    @Column(name = "team_b_score", nullable = false)
    private Integer teamBScore;

    // 1 Trận đấu có nhiều người tham gia (thường là 4 người)
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private List<MatchParticipant> participants;
}