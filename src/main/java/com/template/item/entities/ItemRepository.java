package com.template.item.entities;

import com.template.user.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.id=:id")
    Optional<Item> fetchById(@Param("id") Long id);

    @Query("SELECT i FROM Item as i WHERE i.id IN (:ids)")
    Set<Item> fetchByIds(@Param("ids") Set<Long> ids);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.isApproved=true",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.isApproved=true")
    Page<Item> fetchApproved(Pageable pageable);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.isApproved=false",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.isApproved=false")
    Page<Item> fetchUnapproved(Pageable pageable);

    @Query(value = "SELECT i FROM Item as i",
            countQuery = "SELECT count(i) FROM Item as i")
    Page<Item> fetchAll(Pageable pageable);

    @Query("SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.user=:author")
    Set<Item> fetchAuthorsItems(@Param("author") UserEntity author);

    @Query("SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c JOIN FETCH i.user as u WHERE i.id=:id")
    Optional<Item> fetchFullDataById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Item i SET i.isApproved=:approved WHERE i.id IN (:ids)")
    void approve(@Param("ids") List<Long> ids, @Param("approved") boolean approved);
}
