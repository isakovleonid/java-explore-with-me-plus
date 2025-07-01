package ru.practicum.users.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.in.NewUserRequest;
import ru.practicum.users.dto.output.UserDto;
import ru.practicum.users.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
@Slf4j
public class UserAdminController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false, defaultValue = "") List<Integer> ids,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("GET /admin/users - Getting users");
        return service.getAll(ids, from, size);
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST /admin/users - Add user: {}", newUserRequest);
        return service.add(newUserRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("DELETE /admin/users/{} - Delete user by id", id);
        service.delete(id);
    }
}
