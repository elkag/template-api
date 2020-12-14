package com.template.item.service;

import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.models.SearchResultDTO;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserPrincipal;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;

public interface ItemService {

    ItemDTO addItem(final ItemDTO model, final UserEntity user);

    boolean deleteItem(final long id, UserPrincipal principal) throws EntityNotFoundException;
    boolean deleteItem  (final long id) throws EntityNotFoundException;

    @Transactional
    ItemDTO updateItem(final ItemDTO model, final UserPrincipal principal) throws EntityNotFoundException;

    List<ItemDTO> getAll();

    PageDTO getApproved(int pageNumber, int pageSize);
    PageDTO getNotApproved(int pageNumber, int pageSize);

    ItemDTO approve(long id);

    ItemDTO getItem(long id);

    ItemDTO getApprovedById(long id);

    @Transactional
    SearchResultDTO search(String text, int pageNumber, int pageSize);
}
