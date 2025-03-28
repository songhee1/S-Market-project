package com.flab.s_market.domains.security.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

public class CustomUserDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = 174726374856727L;

    private Long id;
    private String email;
    private String name;
    private String password;
    private Collection<GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String email, String name, String password, String roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities = createAuthorities(roles);
    }

    private Collection<GrantedAuthority> createAuthorities(String roles) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for(String role : roles.split(",")){
            if (!StringUtils.hasText(role)) continue;
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

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
