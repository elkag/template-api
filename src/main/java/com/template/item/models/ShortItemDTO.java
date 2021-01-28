package com.template.item.models;

import com.template.user.models.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ShortItemDTO {

    private Long id;
    private String name;
    private String description;
    private String notes;
    private String link;
    private boolean approved;
    private LocalDateTime creationDate;
    private UserDTO user;

}