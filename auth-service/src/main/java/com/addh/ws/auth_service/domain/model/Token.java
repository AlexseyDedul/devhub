package com.addh.ws.auth_service.domain.model;

import com.addh.ws.auth_service.domain.enums.TokenType;
import com.addh.ws.auth_service.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    @Id
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UuidUtils.timeBasedUuid();
        }
    }

    @Column(unique = true, length = 1000)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;
    private boolean revoked;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
