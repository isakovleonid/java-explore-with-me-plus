package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.exceptions.DuplicateException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.dto.in.NewUserRequest;

import ru.practicum.users.dto.output.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    @Override
    public List<UserDto> getAll(List<Integer> ids, int from, int size) {
        return repository.findUsersByIds(ids, from, size)
                .stream()
                .map(mapper::toUserDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        if (repository.existsByEmail(newUserRequest.getEmail())) {
            throw new DuplicateException("Email already exists emile: " +  newUserRequest.getEmail());
        }
        User user = repository.save(mapper.toUser(newUserRequest));
        log.info("User was created: {}", user);
        return mapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("User with id=%d was not found", id));
        }
        repository.deleteById(id);
        log.info("User with id={}, was deleted", id);
    }
}