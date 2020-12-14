package com.template.item.service.impl;

import com.template.category.entity.Category;
import com.template.category.service.CategoryService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    @Transactional
    public Item saveItem(Item item) {
        Set<Category> categories = saveCategoriesIfNotExist(item.getCategories());
        Set<Tag> tags = saveTagsIfNotExist(item.getTags());

        item.setCategories(categories);
        item.setTags(tags);

        return itemRepository.saveAndFlush(item);
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
