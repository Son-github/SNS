package com.sonny.sns.model;

import com.sonny.sns.model.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class User implements UserDetails {
    private Integer id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp modifiedAt;
    private Timestamp removedAt;


    public static User fromEntity(UserEntity entity) { // User를 Dto로 변환
        return new User(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getRemovedAt()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return removedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return removedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return removedAt == null;
    }

    @Override
    public boolean isEnabled() {
        return removedAt == null;
    }
}
