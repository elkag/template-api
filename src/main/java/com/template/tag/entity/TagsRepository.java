package com.template.tag.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagsRepository extends JpaRepository<Tag, String> {
    Optional<Tag> findByName(String name);
}
