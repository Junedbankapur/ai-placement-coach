package com.interviewcoach.security;

import com.interviewcoach.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

/**
 * CustomUserDetails - Bridges our JPA User Entity and Spring Security's internal UserDetails interface.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - UserDetails is an interface required by Spring Security to manage user metadata, active passwords, and authorities.
 * - SimpleGrantedAuthority: Wraps our user's role (e.g. ROLE_USER) as a Spring Security authority so it can restrict access.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Maps our user role string to Spring Security's Authority type
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Account state flags. In a real-world app, you might map these to database flags.
    // For now, we return true to indicate the account is fully functional.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
