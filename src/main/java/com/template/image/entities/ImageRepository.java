package com.template.image.entities;

import com.template.item.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image as i WHERE i.item=:item")
    List<Image> fetchImagesByItem(@Param("item") Item item);

    @Query("SELECT i FROM Image as i JOIN FETCH i.item as t WHERE i.id=:imageId AND i.item.id=:itemId")
    List<Image> fetchImageByItemId(@Param("itemId") Long itemId, @Param("imageId") Long imageId );

    @Query("SELECT i FROM Image as i WHERE i.id IN (:imageIds)")
    List<Image> fetchImages(@Param("imageIds") List<Long> imageIds);
}
