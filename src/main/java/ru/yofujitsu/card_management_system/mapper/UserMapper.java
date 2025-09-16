package ru.yofujitsu.card_management_system.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.user.User;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final CardMapper cardMapper;

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getCards().stream().map(cardMapper::toCardDto).toList()
        );
    }
}
