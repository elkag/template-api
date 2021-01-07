package com.template.item.rest;

import com.template.item.models.ApproveItemRequest;
import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items/admin")
@Validated
public class AdminItemController extends BaseItemController {

    public AdminItemController(ItemService itemService, AddItemService addItemService) {
        super(itemService, addItemService);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/get-all")
    public ResponseEntity<List<ItemDTO>> getAll() {
        List<ItemDTO> response = itemService.getAll();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/approve")
    public ResponseEntity<Set<ItemDTO>> approveItem( @RequestBody @Valid Set<@Valid ApproveItemRequest> items) {
        try{
            Set<ItemDTO> response = itemService.approve(items);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.noContent().build();
        }
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<ItemDTO> get(@RequestParam("id") long id) {
        ItemDTO response = itemService.getItem(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/notapproved")
    public ResponseEntity<PageDTO> getNotApproved(@RequestParam("p") int page,
                                                  @RequestParam("s") int size) {
        return ResponseEntity.ok(itemService.getNotApproved(page, size));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<PageDTO> getAlld(@RequestParam("p") int page,
                                                  @RequestParam("s") int size) {
        return ResponseEntity.ok(itemService.getAll(page, size));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteItem(@RequestParam final Long id) {
        try {
            itemService.deleteItem(id);
        } catch (EntityNotFoundException ex){
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Item entity does not exist");
        }
        return ResponseEntity.ok().build();
    }
}
