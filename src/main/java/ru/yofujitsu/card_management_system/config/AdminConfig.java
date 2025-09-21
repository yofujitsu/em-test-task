package ru.yofujitsu.card_management_system.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;
import ru.yofujitsu.card_management_system.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class AdminConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод инициализации пользователя-администратора в БД
     */
    @PostConstruct
    public void initAdminUser() {
        userRepository.findByUsername("admin").ifPresentOrElse(
                user -> {},
                () -> userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("AdminPass123"))
                        .role(UserRole.ADMIN)
                        .build())
        );
    }
}
