package com.prodactivv.app.core.security;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.InvalidCredentialsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserDTO;
import com.prodactivv.app.core.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collections;

@Service
public class AuthService {

    public static final String USERKEY_VALUES_DELIMITER = ":";
    public static final int CREDENTIALS_EMAIL = 0;
    public static final int CREDENTIALS_PASSWORD = 1;
    public static final String USER_NOT_FOUND_MSG = "User not found: %s";

    private final UserRepository repository;
    private final TokenValidityService tokenValidityService;

    public AuthService(UserRepository repository, TokenValidityService tokenValidityService) {
        this.repository = repository;
        this.tokenValidityService = tokenValidityService;
    }

    public AuthResponse generateToken(String userKey) throws NotFoundException, InvalidCredentialsException, DisintegratedJwsException {
        String[] credentials = new String(Base64.getDecoder().decode(userKey)).split(USERKEY_VALUES_DELIMITER);
        User user = repository.findUserByEmail(credentials[CREDENTIALS_EMAIL])
                .orElseThrow(new NotFoundException(
                        String.format(USER_NOT_FOUND_MSG, credentials[CREDENTIALS_EMAIL])
                ));

        if (credentials[CREDENTIALS_PASSWORD].equalsIgnoreCase(user.getPassword())) {
            return generateTokenForUser(user);
        }

        throw new InvalidCredentialsException();
    }

    public AuthResponse getTokenData(String token) throws NotFoundException, DisintegratedJwsException {
        TokenValidity tokenValidity = tokenValidityService.getTokenValidity(token);
        UserDTO userDTO = getUser(token);

        return AuthResponse.builder()
                .userEmail(userDTO.getEmail())
                .userRole(userDTO.getRole())
                .userId(userDTO.getId())
                .validUntil(tokenValidity.getUntil())
                .token(token)
                .build();
    }

    public UserDTO getUser(String token) throws NotFoundException, DisintegratedJwsException {
        return tokenValidityService.getUser(token);
    }

    public AuthResponse generateTokenForUser(User user) throws NotFoundException, DisintegratedJwsException {
        return getTokenData(
                tokenValidityService
                        .createTokenValidityForUser(user)
                        .getToken()
        );
    }

    public boolean hasUserLevelAccess(String token, String url) throws NotFoundException {
        return url.startsWith("/user")
                && tokenValidityService.hasUserLevelAccess(tokenValidityService.getTokenValidity(token));
    }

    public boolean hasAdminLevelAccess(String token, String url) throws NotFoundException {
        return url.startsWith("/admin")
                && tokenValidityService.hasAdminLevelAccess(tokenValidityService.getTokenValidity(token));
    }

    public UsernamePasswordAuthenticationToken createUsernamePasswordAuthToken(String token) throws NotFoundException, DisintegratedJwsException {
        UserDTO user = tokenValidityService.getUser(token);
        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                token,
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public boolean userHasAccessToRequest(HttpServletRequest request, String token) {
        try {
            if (tokenValidityService.isTokenValid(token)) {
                return hasAdminLevelAccess(token, request.getRequestURI())
                        || hasUserLevelAccess(token, request.getRequestURI());
            }
        } catch (NotFoundException e) {
            return false;
        }

        return false;
    }

    public void refreshToken(String token) throws NotFoundException {
        tokenValidityService.refreshToken(token);
    }
}
