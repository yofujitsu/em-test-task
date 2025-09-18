package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;
import ru.yofujitsu.card_management_system.exception.UserNotFoundException;
import ru.yofujitsu.card_management_system.mapper.UserMapper;
import ru.yofujitsu.card_management_system.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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

    public UserDto getUserDtoByUsername(String username) {
        User user = getUserByUsername(username);
        return userMapper.toUserDto(user);
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseGet(() -> userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found with username: %s".formatted(username)))
        );
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserDto);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void removeUser(String username) {
        userRepository.delete(getUserByUsername(username));
        log.info("User with username: {} deleted", username);
    }
}
