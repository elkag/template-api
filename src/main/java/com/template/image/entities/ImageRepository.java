package com.template.image.entities;

import com.template.item.entities.Item;
import com.template.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image as i WHERE i.item=:item")
    List<Image> fetchImagesByItem(@Param("item") Item item);

    @Query("SELECT i FROM Image as i WHERE i.id IN (:imageIds)")
    List<Image> fetchImages(@Param("imageIds") List<Long> imageIds);
}
