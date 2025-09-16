package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yofujitsu.card_management_system.dto.user.UserCreateDto;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;
import ru.yofujitsu.card_management_system.exception.UserNotFoundException;
import ru.yofujitsu.card_management_system.mapper.CardMapper;
import ru.yofujitsu.card_management_system.mapper.UserMapper;
import ru.yofujitsu.card_management_system.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserById(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseGet(() -> userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id: %s".formatted(userId)))
        );
    }

    public void createUser(UserCreateDto userCreateDto) {
        User user = User.builder()
                .email(userCreateDto.email())
                .password(userCreateDto.password())
                .username(userCreateDto.username())
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
        log.info("User with role USER created with email: {}", user.getEmail());
    }

    public UserDto getUserDtoById(UUID userId) {
        User user = getUserById(userId);
        return userMapper.toUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }
}
