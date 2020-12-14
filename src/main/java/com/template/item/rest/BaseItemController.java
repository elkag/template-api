package com.template.item.rest;

import com.template.item.models.PageDTO;
import com.template.item.models.SearchResultDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class BaseItemController {
    protected final ItemService itemService;
    protected final AddItemService addItemService;

    public BaseItemController(ItemService itemService, AddItemService addItemService) {
        this.itemService = itemService;
        this.addItemService = addItemService;
    }

    public ResponseEntity<PageDTO> getApproved(@RequestParam("p") int page,
                                               @RequestParam("s") int size) {

        return ResponseEntity.ok(itemService.getApproved(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> search(@RequestParam("t") String text,
                                                @RequestParam("p") int page,
                                                @RequestParam("s") int size) {
        SearchResultDTO response = itemService.search(text, page, size);
        return ResponseEntity.ok(response);
    }

}
