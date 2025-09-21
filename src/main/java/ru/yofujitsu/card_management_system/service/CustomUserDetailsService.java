package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.repository.UserRepository;
import ru.yofujitsu.card_management_system.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Метод находит пользователя по его юзернейму
     * Метод вызывается фреймворком Spring Security в процессе аутентификации
     *
     * @param username юзернейм пользователя
     * @return заполненная запись пользователя {@link CustomUserDetails})
     * @throws UsernameNotFoundException если пользователь не был найден или у него нет прав доступа
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
        return CustomUserDetails.getUser(user);
    }
}
