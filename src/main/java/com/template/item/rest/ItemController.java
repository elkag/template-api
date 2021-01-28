package com.template.item.rest;

import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.models.SearchResultDTO;
import com.template.item.models.ValuesAllowed;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import com.template.user.entities.UserPrincipal;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/items")
@Api(value="items-controller", tags = {"Items controller"})
@Validated
public class ItemController extends BaseItemController {

    public ItemController(ItemService itemService, AddItemService addItemService) {
        super(itemService, addItemService);
    }

    @GetMapping("/get")
    @ApiOperation(value = "Get approved item by ID",
            notes = "This method gets approved item by ID")
    public ResponseEntity<ItemDTO> get(
            @RequestParam("id")
            @ApiParam(
                    name =  "id",
                    type = "long",
                    value = "Item ID",
                    required = true)
            @Valid long id) {

        ItemDTO response = itemService.getApprovedById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping("/get-author-item")
    @ApiOperation(value = "Get owner's item by ID",
            notes = "This method gets owner's item by ID.\nAuthorization is required",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ItemDTO> getAuthorItem(
            @ApiParam(
                    name = "id",
                    type = "long",
                    value = "Item ID",
                    required = true)
            @RequestParam("id") long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ItemDTO response = itemService.getAuthorsItemById(id, userPrincipal.getUserEntity());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PutMapping("/unused-update")
    @ApiOperation(value = "Create a new item",
            notes = "This method creates a new item or update existing item.\nAuthorization is required",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ItemDTO> addItem(
            @RequestBody
            @ApiParam(
                    name =  "itemModel",
                    type = "Object",
                    value = "ItemDTO",
                    required = true) final ItemDTO itemModel,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ItemDTO response = itemService.addItem(itemModel, userPrincipal.getUserEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping("/get-author-items")
    @ApiOperation(value = "Get author's item",
            notes = "This method get author's item",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<PageDTO> getAuthorItems(@AuthenticationPrincipal final UserPrincipal userPrincipal,
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
                                                  ) int size,
                                                  @RequestParam(value = "order", required = false)
                                                  @ValuesAllowed(propName = "order", values = {
                                                          "user",
                                                          "date",
                                                          "approved",
                                                          "name"
                                                  })
                                                  @ApiParam(
                                                          name =  "order",
                                                          type = "String",
                                                          value = "Order could be by item's owner full name \"user\", \"date\", is \"approved\" or item's \"name\":",
                                                          example = "user",
                                                          allowableValues = "user, name, date",
                                                          defaultValue = "date"
                                                  ) String order,
                                                  @RequestParam(value = "dir", required = false)
                                                  @ValuesAllowed(propName = "dir", values = {
                                                          "asc",
                                                          "desc"
                                                  })
                                                  @ApiParam(
                                                          name =  "dir",
                                                          type = "String",
                                                          value = "Order direction (ASC or DESC):",
                                                          example = "asc",
                                                          allowableValues = "asc, desc",
                                                          defaultValue = "desc"
                                                  ) String  direction) {

        PageDTO response = itemService.getAuthorItems(userPrincipal.getUserEntity(), page, size, order, direction);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete an item",
            notes = "This method delete an item",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> deleteItem(@RequestParam
                                        @ApiParam(
                                                name =  "id",
                                                type = "long",
                                                value = "Item ID",
                                                required = true
                                        )
                                        @Valid final Long id,
                                        @AuthenticationPrincipal UserPrincipal principal) {
        itemService.deleteItem(id, principal.getUserEntity());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PutMapping("/update")
    @ApiOperation(value = "Update an existing item",
            notes = "This method update an existing item.\nAuthorization is required",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> updateItem(
            @RequestBody
            @ApiParam(
                    name =  "itemDTO",
                    type = "Object",
                    value = "Object containing some data about the item.",
                    required = true) final ItemDTO itemDTO,
            @AuthenticationPrincipal final UserPrincipal principal) {
        ItemDTO response = itemService.updateItem(itemDTO, principal.getUserEntity());
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/new")
    @ApiOperation(value = "Creates a new empty item",
            notes = "This method creates an empty item.\n" +
                    "Authorization is required.",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<Long> createNewEmptyItem(@AuthenticationPrincipal final UserPrincipal principal) {
        Long response = addItemService.createEmptyItem(principal.getUserEntity());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/get-all")
    @ApiOperation(value = "Get items with status APPROVED",
            notes = "This method gets a page with items with status APPROVED")
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
        return super.getApproved(page, size);
    }

    @ApiOperation(
            value = "Search for an item containing some string.\n",
            notes = "Search for a string in the following fields:\n" +
                    "Item's name, description, categories and tags")
    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> search(
            @RequestParam("t")
            @NotNull
            @ApiParam(
                    name =  "t",
                    type = "String",
                    value = "The text you are looking for",
                    example = "string",
                    required = true)
            @Valid String text,
            @RequestParam("p")
            @NotNull
            @ApiParam(
                    name =  "p",
                    type = "String",
                    value = "Page number.\n" +
                            "Starts from 0 - [0, 1, ..., n]",
                    example = "1",
                    required = true)
            @Valid int page,
            @RequestParam("s")
            @NotNull
            @ApiParam(
                    name =  "s",
                    type = "Integer",
                    value = "Count of the rows in the page",
                    example = "10",
                    required = true)
            @Valid int size) {
        SearchResultDTO response = itemService.search(text, page, size);
        return ResponseEntity.ok(response);
    }
}