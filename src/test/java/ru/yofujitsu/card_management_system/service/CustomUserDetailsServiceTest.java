package ru.yofujitsu.card_management_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.entity.user.UserRole;
import ru.yofujitsu.card_management_system.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .password("test_password")
                .email("test@example.com")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void testLoadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(testUser.getUsername());

        assertNotNull(userDetails);
        assertEquals(testUser.getUsername(), userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    void testLoadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("non_existent_user"));
    }
}
