package com.template.item.service.impl;

import com.template.category.entity.Category;
import com.template.category.service.CategoryService;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.item.models.ItemDTO;
import com.template.item.service.AddItemService;
import com.template.item.utils.ItemServiceTestUtils;
import com.template.tag.entity.Tag;
import com.template.tag.service.TagService;
import com.template.user.entities.Authority;
import com.template.user.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddItemServiceTest {
    public AddItemService addItemService;

    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private CategoryService mockCategoryService;
    @Mock
    private TagService mockTagService;

    Map<Authority, UserEntity> usersMap;

   @BeforeEach
    public void setup(){

        usersMap = ItemServiceTestUtils.getUsers();
        addItemService = new AddItemServiceImpl(
                mockItemRepository,
                mockCategoryService,
                mockTagService
        );
    }

    @Test
    void addExistingItemTest_assertThrow() {
        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        itemDTO.setId(1L);

        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(ItemServiceTestUtils.getItemWith2Categories2Tags()));

        assertThrows(IllegalArgumentException.class, () -> addItemService.addItem(itemDTO, usersMap.get(Authority.AUTHOR)));
    }

     @Test
    void addAuthorsItemTest_ExpectNotApproved() {
        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        UserEntity user = usersMap.get(Authority.AUTHOR);
        setupMocks_forAddItemTests();

        Item saved = addItemService.addItem(itemDTO, user);

        assertFalse(saved.isApproved());
    }

    @Test
    void addAdminItemTest_ExpectApproved() {
        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        UserEntity user = usersMap.get(Authority.ADMIN);
        setupMocks_forAddItemTests();

        Item saved = addItemService.addItem(itemDTO, user);

        assertTrue(saved.isApproved());
    }

    @Test
    void addAdminItemTest_ExpectNotApproved() {
        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        UserEntity user = usersMap.get(Authority.AUTHOR);
        setupMocks_forAddItemTests();

        Item saved = addItemService.addItem(itemDTO, user);

        assertFalse(saved.isApproved());
    }

    @Test
    void addItemTest_ExpectCorrectSaved() {
        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        UserEntity user = usersMap.get(Authority.ADMIN);
        setupMocks_forAddItemTests();

        Item saved = addItemService.addItem(itemDTO, user);

        assertNotNull(saved.getId());
        assertEquals(itemDTO.getName(), saved.getName());
        assertEquals(user, saved.getUser());
        assertEquals(itemDTO.getDescription(), saved.getDescription());
        assertEquals(itemDTO.getNotes(), saved.getNotes());
        assertEquals(itemDTO.getCategories().size(), saved.getCategories().size());
        assertEquals(itemDTO.getTags().size(), saved.getTags().size());
    }

    private void setupMocks_forAddItemTests() {
        when(mockCategoryService.getOrSaveByName(any())).thenAnswer((Answer<Category>) invocation -> {
            Category category = new Category();
            category.setId(1L);
            category.setName(invocation.getArgument(0));
            return category;
        });
        when(mockTagService.getOrSaveByName(any())).thenAnswer((Answer<Tag>) invocation -> {
            Tag tag = new Tag();
            tag.setId(1L);
            tag.setName(invocation.getArgument(0));
            return tag;
        });
        when(mockItemRepository.saveAndFlush(any())).thenAnswer((Answer<Item>) invocation -> {
            ((Item) invocation.getArgument(0)).setId(1L);
            return (Item) invocation.getArgument(0);
        });
    }
}