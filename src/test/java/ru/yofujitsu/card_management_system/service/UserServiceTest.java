package ru.yofujitsu.card_management_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;
import ru.yofujitsu.card_management_system.exception.UserNotFoundException;
import ru.yofujitsu.card_management_system.mapper.UserMapper;
import ru.yofujitsu.card_management_system.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .email("test@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
        testUserDto = new UserDto(testUser.getId(), testUser.getEmail(), testUser.getUsername(), testUser.getRole(), List.of());
    }

    @Test
    void testCreateUser_ShouldSaveNewUser() {
        String username = "new_user";
        String email = "new@example.com";
        String password = "new_password";
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.createUser(username, email, password);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserDtoByUsername_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserDtoByUsername(testUser.getUsername());

        assertNotNull(result);
        assertEquals(testUserDto.username(), result.username());
    }

    @Test
    void testGetUserDtoByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDtoByUsername(testUser.getUsername()));
    }

    @Test
    void testGetAllUsers_ShouldReturnPageOfUserDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testUserDto.username(), result.getContent().get(0).username());
    }

    @Test
    void testExistsByUsername_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        boolean exists = userService.existsByUsername("existing_user");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("existing@example.com");

        assertTrue(exists);
    }
}
