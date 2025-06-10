package com.addh.ws.auth_service.infrastructure.persistence.jpa;

import com.addh.ws.auth_service.domain.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTokenRepository extends JpaRepository<Token, UUID> {
    @Query("""
        select t from Token t inner join t.user u 
        where u.id = :userId and (t.expired = false and t.revoked = false)
        """)
    List<Token> findAllValidTokensByUserId(UUID userId);

    Optional<Token> findByToken(String token);
}
