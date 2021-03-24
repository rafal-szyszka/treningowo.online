package com.prodactivv.app.core.security;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO.SimpleWorkoutPlanView;
import com.prodactivv.app.admin.trainer.workout.UsersWorkoutPlanService;
import com.prodactivv.app.core.events.Event;
import com.prodactivv.app.core.events.EventService;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.InvalidCredentialsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDiet;
import com.prodactivv.app.user.model.UserDietRepository;
import com.prodactivv.app.user.model.UserRepository;
import com.prodactivv.app.user.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String USERKEY_VALUES_DELIMITER = ":";
    public static final int CREDENTIALS_EMAIL = 0;
    public static final int CREDENTIALS_PASSWORD = 1;
    public static final String USER_NOT_FOUND_MSG = "User not found: %s";

    private final UserRepository repository;
    private final TokenValidityService tokenValidityService;
    private final UserSubscriptionService subscriptionService;
    private final UsersWorkoutPlanService workoutPlanService;
    private final UserDietRepository dietRepository;
    private final EventService eventService;

    @Value("${app.security.access.control.user-level.url.prefix}")
    private String accessControlUserLevelUrlPrefix;
    @Value("${app.security.access.control.admin-level.url.prefix}")
    private String accessControlAdminLevelUrlPrefix;
    @Value("${app.security.access.control.public-level.url.prefix}")
    private String accessControlPublicLevelUrlPrefix;

    public AuthResponse generateToken(String userKey) throws NotFoundException, InvalidCredentialsException, DisintegratedJwsException, UserNotFoundException {
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

    public TokenValidity.Dto.TokenResponse restoreToken(String shortToken) throws NotFoundException {
        Event event = eventService.getEventByHash(shortToken);
        User user = event.getUser();
        TokenValidity tokenValidity = tokenValidityService.createTokenValidityForUser(user);
        return TokenValidity.Dto.TokenResponse.builder().token(tokenValidity.token).build();
    }

    public AuthResponse getTokenData(String token) throws NotFoundException, DisintegratedJwsException, UserNotFoundException {
        TokenValidity tokenValidity = tokenValidityService.getTokenValidity(token);
        User.Dto.Simple userDto = getUser(token);

        if (User.Roles.isUser(userDto.getRole())) {
            return AuthResponse.builder()
                    .userEmail(userDto.getEmail())
                    .userRole(userDto.getRole())
                    .validUntil(tokenValidity.getUntil())
                    .token(token)
                    .user(userDto)
                    .subscriptions(subscriptionService.getUserSubscriptions(userDto))
                    .workoutPlans(workoutPlanService.getUserWorkoutPlans(userDto.getId()).stream().map(SimpleWorkoutPlanView::of).collect(Collectors.toList()))
                    .diets(dietRepository.findAllUserDiets(userDto.getId()).stream().map(UserDiet.Dto.Diet::fromUserDiet).collect(Collectors.toList()))
                    .build();
        } else {
            return AuthResponse.builder()
                    .userEmail(userDto.getEmail())
                    .userRole(userDto.getRole())
                    .user(userDto)
                    .validUntil(tokenValidity.getUntil())
                    .token(token)
                    .build();
        }
    }

    public User.Dto.Simple getUser(String token) throws NotFoundException, DisintegratedJwsException {
        return tokenValidityService.getUser(token);
    }

    public AuthResponse generateTokenForUser(User user) throws NotFoundException, DisintegratedJwsException, UserNotFoundException {
        return getTokenData(
                tokenValidityService
                        .createTokenValidityForUser(user)
                        .getToken()
        );
    }

    public boolean hasUserLevelAccess(String token, String url) throws NotFoundException {
        return url.startsWith(accessControlUserLevelUrlPrefix)
                && tokenValidityService.hasUserLevelAccess(tokenValidityService.getTokenValidity(token));
    }

    public boolean hasAdminLevelAccess(String token, String url) throws NotFoundException {
        return url.startsWith(accessControlAdminLevelUrlPrefix)
                && tokenValidityService.hasAdminLevelAccess(tokenValidityService.getTokenValidity(token));
    }

    public UsernamePasswordAuthenticationToken createUsernamePasswordAuthToken(String token) throws NotFoundException, DisintegratedJwsException {
        User.Dto.Simple user = tokenValidityService.getUser(token);
        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                token,
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public UsernamePasswordAuthenticationToken createPublicAuthToken() {
        return new UsernamePasswordAuthenticationToken(
                "public@prodactivv.com",
                "00000",
                Collections.singleton(new SimpleGrantedAuthority(User.Roles.USER.getRoleName()))
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

    public boolean isPublicEndpoint(String url) {
        return url.startsWith(accessControlPublicLevelUrlPrefix);
    }

    public void refreshToken(String token) throws NotFoundException {
        tokenValidityService.refreshToken(token);
    }
}
