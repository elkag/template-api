package com.template.item.service.impl;

import com.template.exceptions.HttpUnauthorizedException;
import com.template.image.dto.ImageDto;
import com.template.image.entities.Image;
import com.template.image.mapper.ImageMapper;
import com.template.image.service.ImageService;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.item.mappers.ItemMapper;
import com.template.item.models.ApproveItemRequest;
import com.template.item.models.ItemDTO;
import com.template.item.models.PageDTO;
import com.template.item.models.SearchResultDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
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
    private final ImageService imageService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           AddItemService addItemService,
                           ImageService imageService,
                           EntityManager entityManager) {

        this.itemRepository = itemRepository;
        this.addItemService = addItemService;
        this.imageService = imageService;
        this.entityManager = entityManager;
    }

    @Override
    public ItemDTO addItem(final ItemDTO model, final UserEntity user) {
        Item item = addItemService.addItem(model, user);
        List<Image> saved = imageService.addImages(item, model.getImages());
        item.setImages(saved);
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
        imageService.deleteAll(itemOpt.get());
        itemRepository.delete(itemOpt.get());

        log.info(String.format("USER_%s: Delete item END: {} -> %d", principal.getUserEntity().getId(), id));
        return true;
    }

    @Override
    public boolean deleteItem(long id) throws EntityNotFoundException {
        log.info(String.format("USER_1: Delete item BEGIN: {} -> %s", id));
        itemRepository.findById(id)
                .ifPresentOrElse(
                        (item) -> {
                            imageService.deleteAll(item);
                            itemRepository.delete(item);
                        },
                        () -> {
                            throw new EntityNotFoundException("Item entity does not exist");
                        }
                );
        log.info(String.format("USER_1: Delete item END: {} -> %d", id));
        return true;
    }

    @Override
    @Transactional
    public ItemDTO updateItem(ItemDTO model, UserEntity userEntity) throws EntityNotFoundException {

        log.info(String.format("USER_%s: Update item BEGIN: {} ->", userEntity.getId()));
        Item updated = addItemService.updateItem(model, userEntity);
        List<Image> images = imageService.getImages(updated);
        // TODO: get images, delete
        // ...
        List<String> publicIds = model.getImages().stream().map(ImageDto::getPublicId).collect(Collectors.toList());
        images.forEach(img -> {
            if(!publicIds.contains(img.getPublicId())) {
                imageService.destroyImages(img);
            }
        });

        List<Image> updatedImages = imageService.getImages(updated);
        updated.setImages(updatedImages);
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(updated);

        log.info(String.format("USER_%s: Update item END: {} ->", userEntity.getId()), itemDTO);
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
    public PageDTO getAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Item> page = itemRepository.fetchAll(pageable);

        return PageDTO.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .result(ItemMapper.INSTANCE.toItemDTOs(page.stream().collect(Collectors.toSet())))
                .build();
    }

    @Override
    @Transactional
    public Set<ItemDTO> approve(Set<ApproveItemRequest> request) {

        List<Long> approved = request.stream()
                .filter(ApproveItemRequest::getIsApproved)
                .map(ApproveItemRequest::getId)
                .collect(Collectors.toList());
        List<Long> disapproved = request.stream()
                .filter(i -> !i.getIsApproved())
                .map(ApproveItemRequest::getId)
                .collect(Collectors.toList());

        itemRepository.approve(approved, true);
        itemRepository.approve(disapproved, false);

        Set<Long> ids = request.stream().map(ApproveItemRequest::getId).collect(Collectors.toSet());
        Set<Item> items = itemRepository.fetchByIds(ids);

        return ItemMapper.INSTANCE.toItemDTOs(items);
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
    public ItemDTO getAuthorsItemById(long id, UserEntity user) {
        Optional<Item> itemOpt = itemRepository.fetchFullDataById(id);
        Item item = itemOpt.orElseThrow(EntityNotFoundException::new);

        List<Image> images = imageService.getImages(item);
        item.setImages(images);

        return ItemMapper.INSTANCE.toItemDTO(item);
    }

    @Override
    public Set<ItemDTO> getAuthorsItems(UserEntity user) {
        Set<Item> items = itemRepository.fetchAuthorsItems(user);

        List<Long> ids = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Image> images = imageService.getImages(ids);

        Set<ItemDTO> itemDTOS = ItemMapper.INSTANCE.toItemDTOs(items);
        itemDTOS.forEach(item ->
                item.setImages(images.stream()
                        .filter(i -> i.getItem().getId().equals(item.getId()))
                        .map(ImageMapper.INSTANCE::toImageDto)
                        .collect(Collectors.toList())));

        return itemDTOS;
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

    @Override
    public Optional<Item> getById(Long itemId) {
        return itemRepository.findById(itemId);
    }

}
