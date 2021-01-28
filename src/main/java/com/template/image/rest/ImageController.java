package com.template.image.rest;

import com.template.image.dto.ImageDto;
import com.template.image.service.ImageService;
import com.template.user.entities.UserPrincipal;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ImageDto> uploadImage(@RequestParam("item")  Long itemId, @RequestPart("file") byte[] image, @AuthenticationPrincipal final UserPrincipal principal) {
        ImageDto result = imageService.upload(image, itemId);
        return ResponseEntity.ok(result);
    }
}
