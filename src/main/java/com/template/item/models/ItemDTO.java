package com.template.item.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class ItemDTO {

    private Long id;
    private String name;
    private String description;
    private String notes;
    private String image;
    private String link;
    private boolean approved;
    private Set<String> categories;
    private Set<String> tags;
}