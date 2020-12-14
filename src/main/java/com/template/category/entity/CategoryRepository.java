package com.template.category.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);
    Set<Category> findByNameIn(Set<String> names);
}
