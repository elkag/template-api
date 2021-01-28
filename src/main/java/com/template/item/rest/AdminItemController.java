package com.template.item.rest;

import com.template.item.models.ApproveItemRequest;
import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.models.ValuesAllowed;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import com.template.user.entities.UserPrincipal;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items/admin")
@Api(value="admin-items-controller", tags = {"Items controller (admin)"})
@Validated
public class AdminItemController extends BaseItemController {

    public AdminItemController(ItemService itemService, AddItemService addItemService) {
        super(itemService, addItemService);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/get-all")
    @ApiOperation(value = "Get all items",
            notes = "Get all items",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<List<ItemDTO>> getAll() {
        List<ItemDTO> response = itemService.getAll();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/approve")
    @ApiOperation(value = "Approve unapproved item",
            notes = "This method change the status of unapproved item to \"approved\"\n" +
                    "User with role SUPER_ADMIN can change the status of any item.\n" +
                    "User with role ADMIN can approve only his own items.\n" +
                    "User with role AUTHOR has no rights to approve items",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<Set<ItemDTO>> approveItem(
            @AuthenticationPrincipal final UserPrincipal principal,
            @RequestBody
            @Valid
            @ApiParam(
                    name =  "changePasswordRequest",
                    value = "Set of ApproveItemRequest",
                    required = true)
            @NotEmpty Set<@Valid ApproveItemRequest> items) {
        Set<ItemDTO> response = itemService.approve(principal, items);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/get")
    @ApiOperation(value = "Get an by item ID",
            notes = "Get an by item ID",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ItemDTO> get(
            @RequestParam("id")
            @NotNull
            @ApiParam(
                   name =  "id",
                   type = "long",
                   value = "Item ID",
                   example = "1",
                   required = true)
            @Valid long id) {
        ItemDTO response = itemService.getItem(id);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Get items that are not approved yet (pageable)",
            notes = "Get a page with items have status \"unapproved\"",
            authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/unapproved")
    public ResponseEntity<PageDTO> getNotApproved(
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
        return ResponseEntity.ok(itemService.getNotApproved(page, size));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/all")
    @ApiOperation(value = "Get a page with items",
            notes = "Get a page with items that have any status (approved or unapproved)",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<PageDTO> getAll(
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
            }) String order,
            @RequestParam(value = "dir", required = false)
            @ValuesAllowed(propName = "dir", values = {
                  "asc",
                  "desc"
            }) String  direction) {
        return ResponseEntity.ok(itemService.getAll(page, size, order, direction));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete an item",
            notes = "This method delete an item",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<?> deleteItem(
            @RequestParam
            @ApiParam(
                    name =  "id",
                    type = "long",
                    value = "Item ID",
                    required = true)
            @Valid final Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}
