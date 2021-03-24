package com.prodactivv.app.core.security;

import com.prodactivv.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenValidityRepository extends JpaRepository<TokenValidity, Long> {

    List<TokenValidity> findAllByUser(User user);

    Optional<TokenValidity> findByToken(String token);

    Optional<TokenValidity> findTokenValidityByShortToken(String shortToken);
}
