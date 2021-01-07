package com.template.item.models;

import com.template.image.dto.ImageDto;
import com.template.image.entities.Image;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class ItemDTO {

    private Long id;
    private String name;
    private String description;
    private String notes;
    private List<ImageDto> images = new ArrayList<>();
    private String link;
    private boolean approved;
    private Set<String> categories;
    private Set<String> tags;
    private LocalDateTime creationDate;

}