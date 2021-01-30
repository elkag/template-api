package com.template.item.service.impl;

import com.template.category.entity.Category;
import com.template.category.service.CategoryService;
import com.template.exceptions.Error;
import com.template.exceptions.HttpBadRequestException;
import com.template.exceptions.HttpUnauthorizedException;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.item.mappers.ItemMapper;
import com.template.item.models.ItemDTO;
import com.template.item.service.AddItemService;
import com.template.tag.entity.Tag;
import com.template.tag.service.TagService;
import com.template.user.entities.Authority;
import com.template.user.entities.UserEntity;
import lombok.extern.log4j.Log4j2;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AddItemServiceImpl implements AddItemService {

    private  final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    public AddItemServiceImpl(
            ItemRepository itemRepository,
            CategoryService categoryService,
            TagService tagService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @Override
    public Long createEmptyItem(UserEntity userEntity) {
        log.info(String.format("USER_%s: Add new item BEGIN: {} -> ", userEntity.getId()));

        Item item = new Item();

        boolean isAdmin = userEntity.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals(Authority.ADMIN.name()));

        item.setUser(userEntity);
        item.setApproved(false);
        item.setCategories(Set.of());
        item.setTags(Set.of());

        Item saved = saveItem(item);

        log.info(String.format("USER_%s: Add new item END: {} -> ", userEntity.getId()), saved.getId());
        return saved.getId();
    }

    @Override
    public Item addItem(ItemDTO model, UserEntity userEntity) {
        log.info(String.format("USER_%s: Add item BEGIN: {} -> ", userEntity.getId()), model);

        Item item = ItemMapper.INSTANCE.toItem(model);

        if(!Objects.isNull(model.getId()) && itemRepository.findById(model.getId()).isPresent()){
            log.warn(String.format("USER_%d: -> ITEM_%d already exist", userEntity.getId(), model.getId()));
            throw new IllegalArgumentException();
        }

       boolean isAdmin = userEntity.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals(Authority.ADMIN.name()));

        item.setUser(userEntity);
        item.setApproved(isAdmin);

        Item saved = saveItem(item);

        log.info(String.format("USER_%s: Add item END: {} -> ", userEntity.getId()), saved);
        return saved;
    }

    @Override
    public Item updateItem(ItemDTO model, UserEntity itemOwner) {
        log.info(String.format("USER_%s: Add item BEGIN: {} -> ", itemOwner.getId()), model);

        Item item = ItemMapper.INSTANCE.toItem(model);

        if(Objects.isNull(model.getId())){
            log.error(String.format("USER_%d: -> ITEM_%d does not exist", itemOwner.getId(), model.getId()));
            throw new EntityNotFoundException();
        }

        Optional<Item> itemOpt = itemRepository.fetchById(model.getId());
        if(itemOpt.isEmpty()){
            throw new EntityNotFoundException();
        }

        Item toUpdate = itemOpt.get();

        boolean isOwner = toUpdate.getUser().equals(itemOwner);
        if(!isOwner) {
            log.error(String.format("USER_%s: is not allowed to edit item: %d",
                    itemOwner.getId(), itemOpt.get().getId()));
            if(!itemOpt.get().getUser().getId().equals(itemOwner.getId())) {
                throw new AccessDeniedException("You have not permission to edit this item.");
            }
        }
        boolean isAdmin = itemOwner.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals(Authority.ADMIN.name()));

        Set<Category> categories = saveCategoriesIfNotExist(item.getCategories());
        Set<Tag> tags = saveTagsIfNotExist(item.getTags());
        toUpdate.setCategories(categories);
        toUpdate.setTags(tags);

        boolean isCompleted = model.getName().isEmpty()
                || model.getDescription().isEmpty()
                || model.getLink().isEmpty()
                || model.getNotes().isEmpty();

        toUpdate.setApproved(isAdmin && isCompleted);
        toUpdate.setName(model.getName());
        toUpdate.setDescription(model.getDescription());
        toUpdate.setLink(model.getLink());
        toUpdate.setNotes(model.getNotes());


        Item saved = saveItem(toUpdate);

        log.info(String.format("USER_%s: Add item END: {} -> ", itemOwner.getId()), saved);
        return saved;
    }

    @Override
    @Transactional
    public Item saveItem(Item item) {
        Set<Category> categories = saveCategoriesIfNotExist(item.getCategories());
        Set<Tag> tags = saveTagsIfNotExist(item.getTags());

        item.setCategories(categories);
        item.setTags(tags);
        Item saved;
        try{
            saved = itemRepository.saveAndFlush(item);
        } catch(ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

            List<String> errors = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());

            throw new HttpBadRequestException(errors, ex.getMessage());
        }
        return saved;
    }

    private Set<Category> saveCategoriesIfNotExist(final Set<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .map(categoryService::getOrSaveByName)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<Tag> saveTagsIfNotExist(final Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .map(tagService::getOrSaveByName)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
