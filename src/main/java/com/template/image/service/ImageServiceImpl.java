package com.template.image.service;

import com.cloudinary.utils.ObjectUtils;
import com.template.config.cloudinary.CloudinaryConfig;
import com.template.image.dto.ImageDto;
import com.template.image.entities.Image;
import com.template.image.entities.ImageRepository;
import com.template.image.mapper.ImageMapper;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    protected final CloudinaryConfig cloudinary;

    public ImageServiceImpl(ImageRepository imageRepository, ItemRepository itemRepository, CloudinaryConfig cloudinary) {
        this.imageRepository = imageRepository;
        this.itemRepository = itemRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public List<Image> getImages(Item item) {
        return imageRepository.fetchImagesByItem(item);
    }

    @Override
    public List<Image> getImages(List<Long> itemIds) {
        return imageRepository.fetchImages(itemIds);
    }

    @Override
    public List<Image> addImages(Item item, List<ImageDto> imageDtos) {
        log.info(String.format("Save %d images for ITEM_%d BEGIN: {} -> %s", imageDtos.size(), item.getId(), imageDtos));
        List<Image> images =  imageDtos.stream()
                .map(imageDto -> {
                    Image image = ImageMapper.INSTANCE.toImage(imageDto);
                    return image.setItem(item);
                })
                .collect(Collectors.toList());
        List<Image> saved = imageRepository.saveAll(images);
        log.info(String.format("Add %d images for ITEM_%d END: {} -> %s", imageDtos.size(), item.getId(), imageDtos));
        return saved;
    }

    @Override
    @Transactional
    public void deleteImage(final Long itemId, final Long imageId) {
        log.info("Delete IMAGE_{} from ITEM_{} BEGIN", imageId, itemId);

        List<Image> images = imageRepository.fetchImageByItemId(itemId, imageId);
        if(images.isEmpty()) {
            throw new EntityNotFoundException("Image not found");
        }

        destroyImage(images.get(0));
        log.info("Delete IMAGE_{} from ITEM_{} END", imageId, itemId);
    }

    @Override
    public void deleteAll(Item item) {
        List<Image> images = imageRepository.fetchImagesByItem(item);
        images.forEach(this::destroyImage);
        imageRepository.deleteAll(images);
    }

    @Override
    public ImageDto upload(byte[] image, Long itemId) {
        Map uploadResult = cloudinary.upload(image,
                ObjectUtils.asMap("resourcetype", "auto"));
        Image uploaded = new Image();
        uploaded.setUrl(uploadResult.get("url").toString());
        uploaded.setPublicId(uploadResult.get("public_id").toString());

        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if(itemOpt.isEmpty()){
            throw new EntityNotFoundException();
        }

        Item item = itemOpt.get();
        uploaded.setItem(item);

        Image saved = imageRepository.save(uploaded);
        return ImageMapper.INSTANCE.toImageDto(saved);
    }

    @Override
    public void destroyImage(Image image) {
        cloudinary.destroy(image.getPublicId(),
                ObjectUtils.asMap("resourcetype", "auto"));

        imageRepository.delete(image);
    }
}
