package com.template.item.entities;

import com.template.user.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item as i " +
            "JOIN FETCH i.user as u " +
            "LEFT JOIN FETCH i.tags as t " +
            "LEFT JOIN FETCH i.categories as c " +
            "WHERE i.id=:id")
    Optional<Item> fetchById(@Param("id") Long id);

    @Query("SELECT i FROM Item as i " +
            "JOIN FETCH i.user as u " +
            "WHERE i.id=:id")
    Optional<Item> fetchAuthorsItemById(@Param("id") Long id);

    @Query("SELECT i FROM Item as i WHERE i.id IN (:ids)")
    Set<Item> fetchByIds(@Param("ids") Set<Long> ids);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.user as u WHERE i.isApproved=true",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.isApproved=true")
    Page<Item> fetchApproved(Pageable pageable);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.user as u WHERE i.isApproved=false",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.isApproved=false")
    Page<Item> fetchNotApproved(Pageable pageable);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.user as u ",
            countQuery = "SELECT count(i) FROM Item as i")
    Page<Item> fetchAll(Pageable pageable);

    @Query(value = "SELECT i FROM Item as i JOIN FETCH i.user as u WHERE i.user=:author",
            countQuery = "SELECT count(i) FROM Item as i WHERE i.user=:author")
    Page<Item> fetchAuthorsItems(@Param("author") UserEntity author, Pageable pageable);

    @Query("SELECT i FROM Item as i JOIN FETCH i.tags as t JOIN FETCH i.categories as c JOIN FETCH i.user as u WHERE i.id=:id")
    Optional<Item> fetchFullDataById(@Param("id") Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i SET i.isApproved=:approved WHERE i.id=:itemId")
    void setApproved(@Param("itemId") Long itemId, @Param("approved") boolean isApproved);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i SET i.isApproved=:approved WHERE i.id=:itemId AND i.user.id=:userId")
    void setApproved(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("approved") boolean isApproved);

    @Query( "SELECT i FROM Item as i " +
            "JOIN FETCH i.user as u " +
            "LEFT JOIN FETCH i.tags as t " +
            "LEFT JOIN FETCH i.categories as c " +
            "WHERE " +
            "i.name = null AND " +
            "i.description = null AND " +
            "i.notes = null AND " +
            "i.link = null AND " +
            "i.creationDate < :date")
    Set<Item> selectOlderThan(@Param("date") LocalDateTime date);

    @Transactional
    @Modifying
    @Query("DELETE FROM Item i WHERE i.id IN (:ids)")
    int deleteIn(@Param("ids") Set<Long> ids);

    @Transactional
    @Modifying
    @Query("DELETE FROM Item i WHERE " +
            "i.name = null AND " +
            "i.description = null AND " +
            "i.notes = null AND " +
            "i.link = null AND " +
            "i.creationDate < :date")
    int deleteOlderThan(@Param("date") LocalDateTime date);
}
