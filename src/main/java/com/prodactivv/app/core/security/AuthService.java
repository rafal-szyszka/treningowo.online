package com.prodactivv.app.core.security;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO.SimpleWorkoutPlanView;
import com.prodactivv.app.admin.trainer.workout.UsersWorkoutPlanService;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.InvalidCredentialsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDTO;
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

import static com.prodactivv.app.user.model.UserSubscriptionDTO.SimpleSubscriptionView.of;

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

    @Value("${app.security.access.control.user-level.url.prefix}")
    private String accessControlUserLevelUrlPrefix;
    @Value("${app.security.access.control.admin-level.url.prefix}")
    private String accessControlAdminLevelUrlPrefix;

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

    public AuthResponse getTokenData(String token) throws NotFoundException, DisintegratedJwsException, UserNotFoundException {
        TokenValidity tokenValidity = tokenValidityService.getTokenValidity(token);
        UserDTO userDTO = getUser(token);

        if (userDTO.getRole().equalsIgnoreCase(User.Roles.USER.getRoleName())) {
            return AuthResponse.builder()
                    .userEmail(userDTO.getEmail())
                    .userRole(userDTO.getRole())
                    .validUntil(tokenValidity.getUntil())
                    .token(token)
                    .user(userDTO)
                    .subscription(of(subscriptionService.getUserActiveSubscriptions(userDTO).orElse(null)))
                    .workoutPlans(workoutPlanService.getUserWorkoutPlans(userDTO.getId()).stream().map(SimpleWorkoutPlanView::of).collect(Collectors.toList()))
                    .build();
        } else {
            return AuthResponse.builder()
                    .userEmail(userDTO.getEmail())
                    .userRole(userDTO.getRole())
                    .user(userDTO)
                    .validUntil(tokenValidity.getUntil())
                    .token(token)
                    .build();
        }
    }

    public UserDTO getUser(String token) throws NotFoundException, DisintegratedJwsException {
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
