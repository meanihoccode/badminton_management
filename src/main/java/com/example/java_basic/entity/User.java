package com.example.java_basic.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.java_basic.enums.Role;
import java.util.Collection;
import java.util.List;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractBaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, MEMBER

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "racket_model")
    private String racketModel;

    // Quan hệ 1 User có nhiều lịch sử giao dịch
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    // Quan hệ 1 User tham gia nhiều trận đấu
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MatchParticipant> matchParticipants;

    @Column(nullable = false)
    private String password; // Thêm trường mật khẩu

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ánh xạ role (ADMIN, MEMBER) sang chuẩn của Spring Security (ROLE_ADMIN, ROLE_MEMBER)
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}

