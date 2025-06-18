package com.example.demo.config;

import com.example.demo.service.JwtService;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.io.IOException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final Pattern USER_ID_PATTERN = Pattern.compile("/api/users/(\\d+)/");

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Extract user ID from URL if present
                String requestURI = request.getRequestURI();
                Matcher matcher = USER_ID_PATTERN.matcher(requestURI);
                
                if (matcher.find()) {
                    String urlUserId = matcher.group(1);
                    Long tokenUserId = jwtService.extractUserId(jwt);
                    System.out.println("URL User ID: " + urlUserId);
                    System.out.println("Token User ID: " + tokenUserId);
                    System.out.println("All Claims: " + jwtService.extractAllClaims(jwt));
                    // If the user IDs don't match, return 403 Forbidden
                    if (tokenUserId == null || !tokenUserId.toString().equals(urlUserId)) {
                        System.out.println("User ID mismatch. URL: " + urlUserId + ", Token: " + tokenUserId);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Access denied: You can only access your own data");
                        return;
                    }
                } else {
                    // Check for userId in query parameter
                    String paramUserId = request.getParameter("userId");
                    if (paramUserId != null) {
                        Long tokenUserId = jwtService.extractUserId(jwt);
                        System.out.println("Query Param User ID: " + paramUserId);
                        System.out.println("Token User ID: " + tokenUserId);
                        System.out.println("All Claims: " + jwtService.extractAllClaims(jwt));
                        if (tokenUserId == null || !tokenUserId.toString().equals(paramUserId)) {
                            System.out.println("User ID mismatch. Query Param: " + paramUserId + ", Token: " + tokenUserId);
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access denied: You can only access your own data");
                            return;
                        }
                    }
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
} 