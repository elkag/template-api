package com.template.item.rest;

import com.template.category.entity.Category;
import com.template.category.entity.CategoryRepository;
import com.template.config.SpringSecurityTestConfig;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.item.utils.ItemServiceTestUtils;
import com.template.tag.entity.Tag;
import com.template.tag.entity.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

public class ItemControllerTestBase {
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected TagsRepository tagsRepository;

    protected Item ADMIN_ITEM;
    protected Item AUTHOR_ITEM;
    protected Item APPROVED_ITEM;

    protected void setUp(){
        createItems();
    }

    private void createItems(){
        ADMIN_ITEM = ItemServiceTestUtils.getItemWith2Categories2Tags();
        ADMIN_ITEM.setUser(SpringSecurityTestConfig.ADMIN);
        ADMIN_ITEM.setCategories(Set.of(new Category("cat 1"), new Category("cat 2")));
        ADMIN_ITEM.setTags(Set.of(new Tag("tag 1"), new Tag("tag 2")));
        ADMIN_ITEM.getCategories().forEach(categoryRepository::save);
        ADMIN_ITEM.getTags().forEach(tagsRepository::save);
        ADMIN_ITEM.setApproved(true);
        ADMIN_ITEM = itemRepository.saveAndFlush(ADMIN_ITEM);

        AUTHOR_ITEM = ItemServiceTestUtils.getItemWith2Categories2Tags();
        AUTHOR_ITEM.setUser(SpringSecurityTestConfig.AUTHOR);
        AUTHOR_ITEM.setCategories(Set.of(new Category("cat 3"), new Category("cat 4")));
        AUTHOR_ITEM.setTags(Set.of(new Tag("tag 3"), new Tag("tag 4")));
        AUTHOR_ITEM.setNotes("notes");
        AUTHOR_ITEM.setDescription("description");
        AUTHOR_ITEM.setLink("http://link");
        AUTHOR_ITEM.setCreationDate(LocalDateTime.now());
        AUTHOR_ITEM.getCategories().forEach(categoryRepository::save);
        AUTHOR_ITEM.getTags().forEach(tagsRepository::save);
        AUTHOR_ITEM.setApproved(false);
        AUTHOR_ITEM = itemRepository.saveAndFlush(AUTHOR_ITEM);

        APPROVED_ITEM = ItemServiceTestUtils.getItemWith2Categories2Tags();
        APPROVED_ITEM.setUser(SpringSecurityTestConfig.ADMIN);
        APPROVED_ITEM.setCategories(Set.of(new Category("cat 5"), new Category("cat 6")));
        APPROVED_ITEM.setTags(Set.of(new Tag("tag 5"), new Tag("tag 6")));
        APPROVED_ITEM.getCategories().forEach(categoryRepository::save);
        APPROVED_ITEM.getTags().forEach(tagsRepository::save);
        APPROVED_ITEM.setApproved(true);
        APPROVED_ITEM = itemRepository.saveAndFlush(APPROVED_ITEM);
    }

    protected void tearDown() {

        itemRepository.deleteAll();
        itemRepository.flush();

        categoryRepository.deleteAll();
        categoryRepository.flush();

        tagsRepository.deleteAll();
        tagsRepository.flush();
    }
}
