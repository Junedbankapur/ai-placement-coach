package com.interviewcoach.security;

import com.interviewcoach.entity.User;
import com.interviewcoach.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService - Loads user-specific data during login or token validation.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - UserDetailsService: This is a core interface in Spring Security. It is responsible for loading 
 *   user credentials (username, password, roles) from the database during authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Query the database for the user. If they don't exist, throw a standard security exception.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        // Return our UserDetails wrapper class
        return new CustomUserDetails(user);
    }
}
