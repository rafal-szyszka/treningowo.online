package com.prodactivv.app.core.security;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwsSecurityFilter extends BasicAuthenticationFilter {

    private final AuthService service;

    public JwsSecurityFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint, AuthService service) {
        super(authenticationManager, authenticationEntryPoint);
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (service.userHasAccessToRequest(request, token)) {
            try {
                SecurityContextHolder.getContext()
                        .setAuthentication(service.createUsernamePasswordAuthToken(token));
                service.refreshToken(token);
                super.doFilterInternal(request, response, chain);
            } catch (NotFoundException | DisintegratedJwsException e) {
                getAuthenticationEntryPoint().commence(
                        request,
                        response,
                        new UnauthorizedException(e.getMessage(), e)
                );
            }
        } else {
            getAuthenticationEntryPoint().commence(
                    request,
                    response,
                    new InsufficientPermissionsException(InsufficientPermissionsException.MESSAGE)
            );
        }
    }

    public static class JwsAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.getWriter()
                    .println(e.getMessage());
        }
    }

    public static class UnauthorizedException extends AuthenticationException {

        public UnauthorizedException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    public static class InsufficientPermissionsException extends AuthenticationException {
        private static final String MESSAGE = "Expired or insufficient permissions!";

        public InsufficientPermissionsException(String msg) {
            super(msg);
        }
    }

}
