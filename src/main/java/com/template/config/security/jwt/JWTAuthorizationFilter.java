package com.template.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.config.security.SecurityConstants;
import com.template.config.security.TokenProvider;
import io.micrometer.core.lang.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Log4j2
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(TokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws IOException, ServletException {

        final UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        String errorMessage = "";
        String detailMessage = "";

        if (authentication == null) {
            try {
                filterChain.doFilter(request, response);
            } catch (ServletException | IOException | RuntimeException e) {
                if (e.getCause() != null) {
                    errorMessage = e.getCause().getMessage();
                } else {
                    errorMessage = e.getMessage();
                }
                final String invalidMsg = (String) request.getAttribute("invalid");
                errorMessage = (invalidMsg != null) ? invalidMsg : errorMessage;

                final Class<Throwable> throwable = Throwable.class;
                try{
                    final Field detailMessageField = throwable.getDeclaredField("detailMessage");
                    detailMessageField.setAccessible(true);
                    detailMessage = (String) detailMessageField.get(e);
                } catch (NoSuchFieldException | NullPointerException | SecurityException | IllegalAccessException exception){
                    throw e;
                }
            } finally {
                if(!errorMessage.isEmpty() || !detailMessage.isEmpty()){
                    System.out.println("requestURL: " + request.getRequestURL());
                    System.out.println("requestURI: " + request.getRequestURI());
                    System.out.println("servletPath: " + request.getServletPath());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    byte[] body = new ObjectMapper()
                            .writeValueAsBytes(
                                    Map.of( "status", String.valueOf(response.getStatus()),
                                            "error", detailMessage,
                                            "message", errorMessage,
                                            "timestamp", Instant.now().toString(),
                                            "path", request.getRequestURI()));
                    response.getOutputStream().write(body);
                    response.flushBuffer();
                }
            }

            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {
            final String jwt = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            if(!tokenProvider.validateToken(jwt, request)) {
                return null;
            }

            String email = tokenProvider.getUsernameFromToken(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            return authentication;
        }

        return null;
    }
}