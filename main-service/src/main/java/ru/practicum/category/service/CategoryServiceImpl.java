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

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto add(NewCategoryDto newCategory) {
        checkCategoryNameExists(newCategory.getName());
        Category category = repository.save(mapper.toCategory(newCategory));
        log.info("Category was created: {}", category);
        return mapper.toCategoryDto(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getCategoryOrThrow(id);
        repository.deleteById(id);
        log.info("Category was deleted: {}", category);
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto newCategory) {
        Category existingCategory = getCategoryOrThrow(id);
        checkCategoryNameExists(newCategory.getName());

        existingCategory.setName(newCategory.getName());

        Category updatedCategory = repository.save(existingCategory);
        log.info("Category was updated: {}", updatedCategory);
        return mapper.toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Category> page = repository.findAll(pageable);

        return page.getContent().stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = getCategoryOrThrow(id);
        return mapper.toCategoryDto(category);
    }

    private void checkCategoryNameExists(String name) {
        if (repository.existsByName(name)) {
            throw new DuplicateException("Category already exists: " + name);
        }
    }

    private Category getCategoryOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
    }
}