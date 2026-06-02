package com.interviewcoach.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * JwtAuthenticationFilter - Intercepts all REST HTTP requests.
 * Extracts the JWT token from the Authorization header, validates it, 
 * and sets the user credentials inside Spring Security's Context.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - OncePerRequestFilter: Guarantees this filter is executed exactly once per request.
 * - SecurityContextHolder: The holder where Spring Security stores details of the currently authenticated user.
 * - Flow:
 *   1. Check "Authorization" header for "Bearer <token>"
 *   2. If valid, fetch UserDetails from the database using CustomUserDetailsService.
 *   3. Build a UsernamePasswordAuthenticationToken and set it in SecurityContextHolder.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extract token from request header
            String jwt = getJwtFromRequest(request);

            // 2. Validate token and load user details
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                // Fetch User Details from MySQL
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                
                // Build an authentication token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 3. Set the context holder so Spring Security recognizes this user is logged in
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Pass the request to the next filter in the security chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts token from the 'Authorization' HTTP request header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Removes "Bearer " prefix and returns the raw token
        }
        return null;
    }
}
