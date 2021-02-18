package com.prodactivv.app.core.security;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;

@Component
public class JwtUtils {

    public static final String CLAIM_ID = "id";
    public static final String CLAIM_ROLE = "role";

    @Value("${app.security.jwt.signing-key}")
    private String signingKey;

    @Value("${app.security.jwt.TTL.hours}")
    private String tokensTTL;

    public String generateTokenForUser(User user) {
        Pair<Key, SignatureAlgorithm> signingKey = getSigningKey();
        return Jwts.builder()
                .claim(CLAIM_ID, String.valueOf(user.getId()))
                .claim(CLAIM_ROLE, String.valueOf(user.getRole()))
                .claim(Claims.ISSUED_AT, LocalDateTime.now().toString())
                .signWith(signingKey.getFirst(), signingKey.getSecond())
                .compact();
    }

    public String obtainClaimWithIntegrityCheck(String jws, String claim) throws DisintegratedJwsException {
        Pair<Key, SignatureAlgorithm> signingKey = getSigningKey();
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey.getFirst())
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();

            return claims.get(claim, String.class);
        } catch (Exception e) {
            throw new DisintegratedJwsException(e.getMessage());
        }
    }

    public boolean checkJwsIntegrity(String jws) {
        Pair<Key, SignatureAlgorithm> signingKey = getSigningKey();
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey.getFirst())
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getTTL() {
        return Long.parseLong(tokensTTL);
    }

    private Pair<Key, SignatureAlgorithm> getSigningKey() {
        return Pair.of(Keys.hmacShaKeyFor(signingKey.getBytes()), SignatureAlgorithm.HS512);
    }
}
