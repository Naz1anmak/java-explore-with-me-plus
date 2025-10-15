package ru.practicum.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest userRequest);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    void checkUser(Long userId);

    void deleteUser(Long userId);

    UserShortDto getUserShortDtoById(Long userId);

    User getUserById(Long userId);
}
