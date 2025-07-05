package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.in.NewCategoryDto;
import ru.practicum.category.dto.output.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.exceptions.DuplicateException;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto add(NewCategoryDto newCategory) {
        if (repository.existsByName(newCategory.getName())) {
            throw new DuplicateException("Category already exists: " +  newCategory.getName());
        }
        Category category = repository.save(mapper.toCategory(newCategory));
        log.info("Category was created: {}", newCategory);
        return mapper.toCategoryDto(category);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", id));
        }
        repository.deleteById(id);
        log.info("Category with id={} was deleted", id);
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto category) {
        Category existingCategory = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));

        existingCategory.setName(category.getName());

        Category updatedCategory = repository.save(existingCategory);
        return mapper.toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Category> page = repository.findAll(pageable);

        return page.getContent().stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Optional<Category> category = repository.findById(id);
        if (category.isEmpty()) {
            throw new NotFoundException(String.format("Category with id=%d was not found", id));
        }
        return mapper.toCategoryDto(category.get());
    }
}