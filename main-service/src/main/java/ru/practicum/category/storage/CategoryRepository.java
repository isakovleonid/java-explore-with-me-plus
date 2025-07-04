package ru.practicum.category.storage;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.category.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
