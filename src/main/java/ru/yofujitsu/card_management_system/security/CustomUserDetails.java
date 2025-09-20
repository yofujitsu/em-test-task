package ru.yofujitsu.card_management_system.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private UserRole userRole;

    /**
     * Метод получения пользовательских данных
     *
     * @param user объект пользователя
     * @return объект {@link CustomUserDetails}
     */
    public static CustomUserDetails getUser(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.userRole.name()));
    }

    @Override public String getPassword() { return this.password; }
    @Override public String getUsername() { return this.username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}