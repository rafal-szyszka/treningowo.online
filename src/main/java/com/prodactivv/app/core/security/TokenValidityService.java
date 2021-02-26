package com.prodactivv.app.core.security;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.User.Roles;
import com.prodactivv.app.user.model.UserDTO;
import com.prodactivv.app.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenValidityService {

    public static final String TOKEN_NOT_FOUND_MSG = "Token expired or invalid";

    private final TokenValidityRepository repository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public TokenValidityService(TokenValidityRepository repository, UserService userService, JwtUtils jwtUtils) {
        this.repository = repository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    public TokenValidity createTokenValidityForUser(User user) {
        List<TokenValidity> userTokens = repository.findAllByUser(user);
        userTokens.forEach(repository::delete);

        return repository.save(
                TokenValidity.builder()
                        .token(jwtUtils.generateTokenForUser(user))
                        .until(LocalDateTime.now().plusHours(1L))
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

    public UserDTO getUser(String token) throws DisintegratedJwsException, NotFoundException {
        Long id = Long.parseLong(jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID));
        return userService.getUserById(id);
    }

    public boolean isTokenValid(String token) throws NotFoundException {
        boolean isTrusted = jwtUtils.checkJwsIntegrity(token);
        TokenValidity tokenValidity = getTokenValidity(token);
        return isTrusted && LocalDateTime.now().isBefore(tokenValidity.getUntil());
    }
}
