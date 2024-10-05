package com.sportZplay.sportZplay.config;

import com.sportZplay.sportZplay.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class UserInfoDetails implements UserDetails {

    /* Login by UserName Or Email*/
    private String userNameOrEmail;

    /* Password */
    private String password;

    /* Authorities */
    private List<GrantedAuthority> authorities;

    /**
     *
     * @param user
     */
    public UserInfoDetails(User user) {
        this.userNameOrEmail = user.getEmailId();
        this.password = user.getPassword();
        this.authorities = List.of(user.getRoles().split(","))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userNameOrEmail;
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
