package ru.practicum.users.service;

import ru.practicum.users.dto.in.NewUserRequest;
import ru.practicum.users.dto.output.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll(List<Integer> ids, int from, int size);

    UserDto add(NewUserRequest newUserRequest);

    void delete(Long id);
}
