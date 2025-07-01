package ru.practicum.users.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.users.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query(value = """
            SELECT *
            FROM users AS u
            WHERE (:ids IS NULL OR u.id IN :ids)
            LIMIT :size
            OFFSET :from
            """, nativeQuery = true)
    List<User> findUsersByIds(@Param("ids") List<Integer> ids, @Param("from") int from, @Param("size") int size);
}