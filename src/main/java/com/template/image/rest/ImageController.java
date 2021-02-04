package com.template.image.rest;

import com.template.image.dto.ImageDto;
import com.template.image.service.ImageService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
@Api(value="users", tags = {"Image controller"})
public class ImageController {


    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/upload")
    public ResponseEntity<ImageDto> uploadImage(@RequestParam("item")  Long itemId, @RequestPart("file") byte[] image) {
        ImageDto result = imageService.upload(image, itemId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestParam("itemId")  Long itemId, @RequestParam("imageId") Long imageId) {
        imageService.deleteImage(itemId, imageId);
        return ResponseEntity.ok().build();
    }
}
