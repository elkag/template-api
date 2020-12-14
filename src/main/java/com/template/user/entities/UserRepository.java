package com.template.user.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findOneByUsername(String username);

  Optional<UserEntity> findById(String id);

}

