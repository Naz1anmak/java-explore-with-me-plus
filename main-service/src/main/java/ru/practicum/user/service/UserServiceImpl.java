package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.UserErrorException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new UserErrorException("Пользователь с таким e-mail уже существует: " + userRequest.email());
        }
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userRequest)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id")
        );
        Page<User> users = (ids == null || ids.isEmpty())
                ? userRepository.findAll(sortedPageable)
                : userRepository.findByIdIn(ids, sortedPageable);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден");
    }

    @Override
    public void deleteUser(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserShortDto getUserShortDtoById(Long userId) {
        checkUser(userId);
        return userMapper.toShortDto(userRepository.findById(userId).get());
    }

    @Override
    public User getUserById(Long userId) {
        checkUser(userId);
        return userRepository.findById(userId).get();
    }

}
