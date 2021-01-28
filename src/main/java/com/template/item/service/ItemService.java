package com.template.item.service;

import com.template.item.entities.Item;
import com.template.item.models.*;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserPrincipal;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemService {

    ItemDTO addItem(final ItemDTO model, final UserEntity user);

    boolean deleteItem(final long id, UserEntity user) throws EntityNotFoundException;
    boolean deleteItem (final long id) throws EntityNotFoundException;

    @Transactional
    ItemDTO updateItem(ItemDTO model, UserEntity userEntity) throws EntityNotFoundException;

    List<ItemDTO> getAll();

    PageDTO getApproved(int pageNumber, int pageSize);
    PageDTO getNotApproved(int pageNumber, int pageSize);
    PageDTO getAll(int pageNumber, int pageSize, String orderBy, String direction);

    @Transactional
    Set<ItemDTO> approve(UserPrincipal principal, Set<ApproveItemRequest> items);

    ItemDTO getItem(long id);

    ItemDTO getApprovedById(long id);

    ItemDTO getAuthorsItemById(long id, UserEntity user);

    PageDTO getAuthorItems(UserEntity user, int pageNumber, int pageSize, String orderBy, String direction);

    @Transactional
    SearchResultDTO search(String text, int pageNumber, int pageSize);

    Optional<Item> getById(Long itemId);

    @Transactional
    void deleteItemsBefore(LocalDateTime now);
}
