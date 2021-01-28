package com.template.item.rest;

import com.template.item.models.PageDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public class BaseItemController {
    protected final ItemService itemService;
    protected final AddItemService addItemService;

    public BaseItemController(ItemService itemService, AddItemService addItemService) {
        this.itemService = itemService;
        this.addItemService = addItemService;
    }

    public ResponseEntity<PageDTO> getApproved(
            @RequestParam("p")
            @NotNull(message = "Page number (p) must not be null.")
            @Min(value = 0, message="Page number (p) must be equal or greater than 0.")
            @ApiParam(
                    name =  "p",
                    type = "Integer",
                    value = "Page number.\n" +
                            "Starts from 0 - [0, 1, ..., n]",
                    example = "1",
                    required = true
            ) int page,
            @RequestParam("s")
            @NotNull(message = "Content size (s) must not be null.")
            @Min(value = 1, message="Content size (s) must be equal or greater than 1.")
            @ApiParam(
                    name =  "s",
                    type = "Integer",
                    value = "Count of the rows in the page",
                    example = "10",
                    required = true
            ) int size) {
        return ResponseEntity.ok(itemService.getApproved(page, size));
    }
}
