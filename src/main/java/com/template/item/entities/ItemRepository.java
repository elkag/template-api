package com.template.item.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.id=:id")
    Optional<Item> fetchById(@Param("id") Long id);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.isApproved=true",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.isApproved=true")
    Page<Item> fetchApproved(Pageable pageable);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c WHERE i.isApproved=false",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.isApproved=false")
    Page<Item> fetchUnapproved(Pageable pageable);

    @Query("SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c JOIN FETCH i.user as u WHERE i.id=:id")
    Optional<Item> fetchFullDataById(@Param("id") Long id);


    @Transactional
    @Modifying
    @Query("UPDATE Item i SET i.isApproved=true WHERE i.id=:id")
    void approve(@Param("id") long id);
}
