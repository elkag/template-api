package com.template.image.service;

import com.template.image.dto.ImageDto;
import com.template.image.entities.Image;
import com.template.item.entities.Item;

import java.util.List;

public interface ImageService {

    List<Image> getImages(Item item);
    List<Image> getImages(List<Long> itemIds);
    List<Image> addImages(Item item, List<ImageDto> imageDtos);
    void deleteImage(Long itemId, Long imageId);
    void deleteAll(Item item);
    ImageDto upload(byte[] image, Long itemId);

    void destroyImage(Image image);
}
