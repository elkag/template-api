package com.template.item.models;

import com.template.image.dto.ImageDto;
import com.template.user.models.UserDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@ApiModel
public class ItemDTO {

    @ApiModelProperty(
            value = "Item ID",
            name = "id",
            example = "1")
    private Long id;

    @ApiModelProperty(
            value = "Item name",
            name = "name",
            example = "Item 1")
    private String name;

    @ApiModelProperty(
            value = "Item description",
            name = "description",
            example = "Item 1 description")
    private String description;

    @ApiModelProperty(
            value = "Item notes",
            name = "notes",
            example = "Item 1 notes")
    private String notes;

    @ApiModelProperty(
            value = "Image links",
            name = "notes",
            example = "[http://link1.com, http://link2.com]")
    private List<ImageDto> images = new ArrayList<>();

    @ApiModelProperty(
            value = "Link",
            name = "link",
            example = "http://")
    private String link;

    @ApiModelProperty(
            value = "Is approved - auto generated",
            hidden = true)
    private boolean approved;

    @ApiModelProperty(
            value = "Categories",
            name = "categories",
            example = "[category 1, category 2]")
    private Set<String> categories;

    @ApiModelProperty(
            value = "Item tags",
            name = "tags",
            example = "[tag 1, tag 2]")
    private Set<String> tags;

    @ApiModelProperty(
            value = "Item creation date - auto generated",
            hidden = true)
    private LocalDateTime creationDate;

    @ApiModelProperty(
            value = "UserDto",
            hidden = true)
    private UserDTO user;

}