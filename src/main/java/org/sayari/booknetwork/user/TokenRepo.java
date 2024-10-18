package org.sayari.booknetwork.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token,Integer> {
    Optional<Token> findTokenByToken(String token);
}
