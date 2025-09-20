package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;
import ru.yofujitsu.card_management_system.exception.UserNotFoundException;
import ru.yofujitsu.card_management_system.mapper.UserMapper;
import ru.yofujitsu.card_management_system.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Метод создания нового пользователя
     *
     * @param username юзернейм пользователя
     * @param email email пользователя
     * @param password зашифрованный пароль
     */
    public void createUser(String username, String email, String password) {
        User user = User.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
        log.info("New user created with email: {}", user.getEmail());
    }

    /**
     * Метод получения dto пользователя по юзернейму
     *
     * @param username юзернейм пользователя
     * @return dto объект пользователя {@link UserDto}
     */
    public UserDto getUserDtoByUsername(String username) {
        User user = getUserByUsername(username);
        return userMapper.toUserDto(user);
    }

    /**
     * Метод получения сущности пользователя по юзернейму
     *
     * @param username юзернейм пользователя
     * @return объект пользователя {@link User}
     */
    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseGet(() -> userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found with username: %s".formatted(username)))
        );
    }

    /**
     * Метод получения всех пользователей системы
     * Используется администратором
     *
     * @param pageable объект пагинации
     * @return страница dto объектов пользователя {@link UserDto}
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserDto);
    }

    /**
     * Метод проверки существования пользователя в системе по юзернейму
     *
     * @param username юзернейм пользователя
     * @return true/false - существует/не существует
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Метод проверки существования пользователя в системе по email
     *
     * @param email email пользователя
     * @return true/false - существует/не существует
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Метод удаления пользователя из системы
     * Используется администратором
     *
     * @param username юзернейм пользователя
     */
    public void removeUser(String username) {
        userRepository.delete(getUserByUsername(username));
        log.info("User with username: {} deleted", username);
    }
}
