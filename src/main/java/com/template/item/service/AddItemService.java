package com.template.item.service;

import com.template.item.entities.Item;
import com.template.item.models.ItemDTO;
import com.template.user.entities.UserEntity;
import org.springframework.transaction.annotation.Transactional;

public interface AddItemService {
    @Transactional
    Item addItem(final ItemDTO dto, UserEntity userEntity);

    Item updateItem(ItemDTO model, UserEntity userEntity);

    @Transactional
    Item saveItem(Item item);

    Long createEmptyItem(UserEntity userEntity);
}
