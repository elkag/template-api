package com.template.item.rest;

import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import com.template.user.entities.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/items")
public class ItemController extends BaseItemController {

    public ItemController(ItemService itemService, AddItemService addItemService) {
        super(itemService, addItemService);
    }

    @GetMapping("/get")
    public ResponseEntity<ItemDTO> get(@RequestParam("id") long id) {

        try {
            ItemDTO response = itemService.getApprovedById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.noContent().build();
        }
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/add")
    public ResponseEntity<ItemDTO> addItem(@RequestBody final ItemDTO model, @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ItemDTO response = itemService.addItem(model, userPrincipal.getUserEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteItem(@RequestParam final Long id,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        itemService.deleteItem(id, principal);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PutMapping("/update")
    public ResponseEntity<?> updateItem(@RequestBody final ItemDTO model, @AuthenticationPrincipal final UserPrincipal principal) {
        ItemDTO response;
        try{
            response = itemService.updateItem(model, principal);
        } catch (EntityNotFoundException ex){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<PageDTO> getApproved(int page, int size) {
        return super.getApproved(page, size);
    }
}