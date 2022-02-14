package com.prodactivv.app.core.security;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.utils.HashGenerator;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.User.Roles;
import com.prodactivv.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenValidityService {

    public static final String TOKEN_NOT_FOUND_MSG = "Token expired or invalid";

    private final TokenValidityRepository repository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final HashGenerator hashGenerator;

    public TokenValidity createTokenValidityForUser(User user) {
        List<TokenValidity> userTokens = repository.findAllByUser(user);
        userTokens.forEach(repository::delete);

        String token = jwtUtils.generateTokenForUser(user);
        return repository.save(
                TokenValidity.builder()
                        .token(token)
                        .shortToken(hashGenerator.generateSha384Hash(Collections.singletonList(token)))
                        .until(LocalDateTime.now().plusHours(jwtUtils.getTTL()))
                        .user(user)
                        .build()
        );
    }

    public TokenValidity getTokenValidity(String token) throws NotFoundException {
        return repository.findByToken(token).orElseThrow(new NotFoundException(TOKEN_NOT_FOUND_MSG));
    }

    public boolean hasAdminLevelAccess(TokenValidity tokenValidity) {
        return tokenValidity.getUser().getRole().equalsIgnoreCase(Roles.ADMIN.getRoleName()) || tokenValidity.getUser().getRole().equalsIgnoreCase(Roles.DIETITIAN.getRoleName());
    }

    public boolean hasUserLevelAccess(TokenValidity tokenValidity) {
        return hasAdminLevelAccess(tokenValidity) || tokenValidity.getUser().getRole().equalsIgnoreCase(Roles.USER.getRoleName());
    }

    public TokenValidity refreshToken(String token) throws NotFoundException {
        return refreshToken(getTokenValidity(token));
    }

    public TokenValidity refreshToken(TokenValidity token) {
        token.setUntil(LocalDateTime.now().plusHours(jwtUtils.getTTL()));
        return repository.save(token);
    }

    public User.Dto.Simple getUser(String token) throws DisintegratedJwsException, NotFoundException {
        Long id = Long.parseLong(jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID));
        return userService.getUserById(id);
    }

    public boolean isTokenValid(String token) throws NotFoundException {
        boolean isTrusted = jwtUtils.checkJwsIntegrity(token);
        TokenValidity tokenValidity = getTokenValidity(token);
        return isTrusted && LocalDateTime.now().isBefore(tokenValidity.getUntil());
    }

    public TokenValidity getTokenValidityByShortToken(String shortToken) throws NotFoundException {
        return repository.findTokenValidityByShortToken(shortToken).orElseThrow(new NotFoundException("Token not found!"));
    }
}
