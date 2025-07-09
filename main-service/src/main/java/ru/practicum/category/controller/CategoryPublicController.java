package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.output.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                    @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("GET /categories - Getting categories");
        return service.findAll(from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable("id") Long id) {
        log.info("GET /categories/{} - Getting category by id", id);
        return service.findById(id);
    }
}