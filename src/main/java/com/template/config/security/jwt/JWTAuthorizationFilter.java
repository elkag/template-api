package com.template.config.security.jwt;

import com.template.config.security.SecurityConstants;
import com.template.config.security.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(TokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
          final HttpServletRequest req,
          final HttpServletResponse res,
          final FilterChain filterChain) throws IOException, ServletException {

        final String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX) ) {
          filterChain.doFilter(req, res);
          return;
        }

        final UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {
            final String jwt = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            if(!tokenProvider.validateToken(jwt)) {
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