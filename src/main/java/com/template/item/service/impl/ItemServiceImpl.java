package com.template.item.service.impl;

import com.template.category.entity.Category;
import com.template.exceptions.HttpUnauthorizedException;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.item.mappers.ItemMapper;
import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.models.SearchResultDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import com.template.tag.entity.Tag;
import com.template.user.entities.Authority;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserPrincipal;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ItemServiceImpl implements ItemService {
    private  final ItemRepository itemRepository;


    private final AddItemService addItemService;
    private final EntityManager entityManager;

    public ItemServiceImpl(ItemRepository itemRepository,
                           AddItemService addItemService,
                           EntityManager entityManager) {

        this.itemRepository = itemRepository;
        this.addItemService = addItemService;
        this.entityManager = entityManager;
    }

    @Override
    public ItemDTO addItem(final ItemDTO model, final UserEntity user) {
        Item item = addItemService.addItem(model, user);
        return ItemMapper.INSTANCE.toItemDTO(item);
    }

    @Override
    public boolean deleteItem(final long id, final UserPrincipal principal) throws EntityNotFoundException {

        log.info(String.format("USER_%s: Delete item BEGIN: {} -> %d", principal.getUserEntity().getId(), id));
        Optional<Item> itemOpt = itemRepository.findById(id);
        if(itemOpt.isEmpty()){
            throw new EntityNotFoundException("Item entity not found.");
        }
        if(!principal.getUserEntity().equals(itemOpt.get().getUser())){
            log.warn(String.format("USER_%s: is not allowed to delete item: %d",
                    principal.getUserEntity().getId(), id));
            throw new HttpUnauthorizedException("Unauthorized request");
        }
        itemRepository.delete(itemOpt.get());

        log.info(String.format("USER_%s: Delete item END: {} -> %d", principal.getUserEntity().getId(), id));
        return true;
    }

    @Override
    public boolean deleteItem(long id) throws EntityNotFoundException {
        log.info(String.format("USER_1: Delete item BEGIN: {} -> %s", id));
        itemRepository.findById(id)
                .ifPresentOrElse(
                        itemRepository::delete,
                        () -> {
                            throw new EntityNotFoundException("Item entity does not exist");
                        }
                );
        log.info(String.format("USER_1: Delete item END: {} -> %d", id));
        return true;
    }

    @Override
    @Transactional
    public ItemDTO updateItem(ItemDTO model, UserPrincipal principal) throws EntityNotFoundException {

        log.info(String.format("USER_%s: Update item BEGIN: {} ->", principal.getUserEntity().getId()));
        Optional<Item> itemOpt = itemRepository.fetchFullDataById(model.getId());
        if(itemOpt.isEmpty()){
            throw new EntityNotFoundException();
        }

        Item item = itemOpt.get();
        boolean isSuperAdmin = principal.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals(Authority.SUPER_ADMIN.name()));
        boolean isOwner = item.getUser().equals(principal.getUserEntity());
        if(!isSuperAdmin && !isOwner) {
            log.error(String.format("USER_%s: is not allowed to edit item: %d",
                   principal.getUserEntity().getId(), item.getId()));
            throw new HttpUnauthorizedException("Unauthorized request");
        }
        Item updated = updateItem(item, model);
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(updated);
        log.info(String.format("USER_%s: Update item END: {} ->", principal.getUserEntity().getId()), itemDTO);
        return itemDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAll() {

        List<Item> items = itemRepository.findAll();
        return items.stream().map( ItemMapper.INSTANCE::toItemDTO).collect(Collectors.toList());
    }

    @Override
    public PageDTO getApproved(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Item> page = itemRepository.fetchApproved(pageable);

        return PageDTO.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .result(ItemMapper.INSTANCE.toItemDTOs(page.stream().collect(Collectors.toSet())))
                .build();
    }

    @Override
    public PageDTO getNotApproved(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Item> page = itemRepository.fetchUnapproved(pageable);

        return PageDTO.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .result(ItemMapper.INSTANCE.toItemDTOs(page.stream().collect(Collectors.toSet())))
                .build();
    }

    @Override
    @Transactional
    public ItemDTO approve(long id) {
        // Optional<Item> itemOpt1 = itemRepository.fetchById(id);
        if(!itemRepository.existsById(id)){
            throw new EntityNotFoundException();
        }
        itemRepository.approve(id);
        entityManager.clear();
        Optional<Item> itemOpt = itemRepository.fetchById(id);
        Item updated = itemOpt.orElseThrow(EntityNotFoundException::new);
        return ItemMapper.INSTANCE.toItemDTO(updated);
    }

    @Override
    public ItemDTO getItem(long id) {
        Optional<Item> itemOpt = itemRepository.fetchById(id);
        if(itemOpt.isEmpty()){
            throw new EntityNotFoundException();
        }
        return ItemMapper.INSTANCE.toItemDTO(itemOpt.get());
    }

    @Override
    public ItemDTO getApprovedById(long id) {
        Optional<Item> itemOpt = itemRepository.fetchById(id);
        Item item = itemOpt.orElseThrow(EntityNotFoundException::new);
        if(!item.isApproved()){
            throw new HttpUnauthorizedException(String.format("%d is not approved", item.getId()));
        }
        return ItemMapper.INSTANCE.toItemDTO(item);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public SearchResultDTO search(String text, int pageNumber, int pageSize) {
        FullTextEntityManager fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);

        //Create a Hibernate Search DSL query builder for the required entity
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Item.class)
                .get();

        //Generate a Lucene query using the builder
        Query query = queryBuilder
                .keyword()
                .onField("name")
                .andField("description")
                .andField("categories.name")
                .andField("tags.name")
                .matching(text)
                .createQuery();

        FullTextQuery fullTextQuery
                = fullTextEntityManager.createFullTextQuery(query, Item.class);

        int count = fullTextQuery.getResultList().size();
        fullTextQuery.setFirstResult((pageNumber - 1) * pageSize);
        fullTextQuery.setMaxResults(pageSize);

        //returns JPA managed entities mapped to DTO
        List<Item> items = fullTextQuery.getResultList();
        List<ItemDTO> resultSet = items.stream()
                .map(ItemMapper.INSTANCE::toItemDTO)
                .collect(Collectors.toList());

        return SearchResultDTO.builder()
                .pages(count/pageSize + (count%pageSize > 0 ? 1 : 0))
                .result(resultSet).build();
    }

    private Item updateItem(Item item, ItemDTO model) {

        item.setName(model.getName());
        item.setDescription(model.getDescription());
        item.setLink(model.getLink());
        item.setNotes(model.getNotes());
        item.setImage(model.getImage());
        item.setTags(model.getTags().stream().map(Tag::new).collect(Collectors.toSet()));
        item.setCategories(model.getCategories().stream().map(Category::new).collect(Collectors.toSet()));

        return addItemService.saveItem(item);
    }

}
