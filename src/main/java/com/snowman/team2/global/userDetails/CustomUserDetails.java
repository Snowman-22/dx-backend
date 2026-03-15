package com.snowman.team2.global.userDetails;

import com.snowman.team2.domain.auth.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Entity에 Role 필드가 없으므로, 모든 사용자에게 USER 권한 부여
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() { return user.getEmail(); }

    public String getName() { return user.getUserName(); }

    public Long getUserId() { return user.getUserId(); }

    public UserEntity getUser() { return this.user; }

    @Override
    public boolean isAccountNonExpired() { return true; }     // 계정 만료 여부: 항상 true

    @Override
    public boolean isAccountNonLocked() { return true; }      // 계정 잠금 여부: 항상 true

    @Override
    public boolean isCredentialsNonExpired() { return true; } // 비밀번호 만료 여부: 항상 true

    @Override
    public boolean isEnabled() { return true; }               // 계정 활성화 여부: 항상 true

}