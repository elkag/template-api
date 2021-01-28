package com.template.user.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findOneByUsername(String username);

  Optional<UserEntity> findById(String id);

  @Query(value =  "SELECT u FROM UserEntity as u " +
                  "LEFT JOIN u.roles as r ON r.role='ADMIN' " +
                  "WHERE r.role IS NULL",

        countQuery =  "SELECT COUNT(u) FROM UserEntity as u " +
                      "LEFT JOIN u.roles as r ON r.role='ADMIN' " +
                      "WHERE r.role IS NULL")
  Page<UserEntity> fetchAuthors(Pageable pageable);

  @Query(value =  "SELECT u FROM UserEntity as u " +
                  "LEFT JOIN u.roles as r ON r.role='ADMIN' " +
                  "WHERE r.role IS NOT NULL AND u.id > 1",

        countQuery =  "SELECT COUNT(u) FROM UserEntity as u " +
                      "LEFT JOIN u.roles as r ON r.role='ADMIN' " +
                      "WHERE r.role IS NOT NULL AND u.id > 1")
  Page<UserEntity> fetchAdmins(Pageable pageable);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = "DELETE FROM AuthorityEntity as r WHERE r.user.id > 1 AND r.user.id IN (:ids) AND r.role='ADMIN'")
  void setDemoted(@Param("ids") List<Long> ids);

}

