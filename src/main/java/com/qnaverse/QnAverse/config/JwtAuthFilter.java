package com.qnaverse.QnAverse.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.qnaverse.QnAverse.services.CustomUserDetailsService;
import com.qnaverse.QnAverse.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This filter reads the 'Authorization' header from every request,
 * extracts the JWT, validates it, and if valid sets the auth in SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        // 1) Get the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2) Check if header is present + starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // remove "Bearer "
            try {
                // 3) Extract username from token
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                // If token is invalid or parsing fails, you can log/ignore
            }
        }

        // 4) If we got a username and SecurityContext is empty, validate
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Make sure token is still valid
            if (jwtUtil.validateToken(token, username)) {
                // 5) If valid, load user from DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 6) Create an auth object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 7) Put auth in security context => user is "logged in"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8) Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
