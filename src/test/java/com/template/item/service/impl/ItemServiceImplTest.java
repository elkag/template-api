package com.template.item.service.impl;

import com.template.exceptions.HttpUnauthorizedException;
import com.template.image.service.ImageService;
import com.template.item.entities.Item;
import com.template.item.entities.ItemRepository;
import com.template.item.models.ItemDTO;
import com.template.item.service.AddItemService;
import com.template.item.service.ItemService;
import com.template.item.utils.ItemServiceTestUtils;
import com.template.user.entities.Authority;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    public ItemService itemService;

    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private AddItemService mockAddItemService;
    @Mock
    private ImageService mockImageService;
    @Mock
    private EntityManager mockEntityManager;

    Map<Authority, UserEntity> usersMap;

    @BeforeEach
    public void setup(){

        usersMap = ItemServiceTestUtils.getUsers();
        itemService = new ItemServiceImpl(
                mockItemRepository,
                mockAddItemService,
                mockImageService, mockEntityManager );
    }

    @Test
    void deleteItemTestBySuperAdmin_ExpectSuccess() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");

        UserEntity user = usersMap.get(Authority.AUTHOR);
        item.setUser(user);

        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> itemService.deleteItem(1L));
    }

    @Test
    void deleteItemTestByItemOwner_ExpectSuccess() {
        UserEntity user = usersMap.get(Authority.AUTHOR);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setUser(user);

        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> itemService.deleteItem(1L, new UserPrincipal(user)));
    }

    @Test
    void deleteItemTestNotByItemOwner_ExpectException() {
        UserEntity user = usersMap.get(Authority.AUTHOR);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setUser(user);

        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(HttpUnauthorizedException.class,
                () -> itemService.deleteItem(1L, new UserPrincipal(usersMap.get(Authority.ADMIN))));
    }

    @Test
    void updateItemBySuperAdminTest_ExpectSuccess() {

        Item old = ItemServiceTestUtils.getItemWith2Categories2Tags();
        old.setUser(usersMap.get(Authority.AUTHOR));

        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        itemDTO.setName("Updated item name");
        itemDTO.setDescription("Updated item description");

        when(mockItemRepository.fetchFullDataById(any())).thenReturn(Optional.of(old));
        when(mockAddItemService.saveItem(any())).thenAnswer((Answer<Item>) invocation -> invocation.getArgument(0));

        ItemDTO updated = itemService.updateItem(itemDTO, usersMap.get(Authority.SUPER_ADMIN));

        assertEquals(old.getId(), updated.getId());
        assertEquals(itemDTO.getName(), updated.getName());
        assertEquals(itemDTO.getDescription(), updated.getDescription());
        assertEquals(2, updated.getCategories().size());
        assertEquals(2, updated.getTags().size());

    }

    @Test
    void updateItemByItemOwnerTest_ExpectSuccess() {
        UserEntity user = usersMap.get(Authority.AUTHOR);
        Item old = ItemServiceTestUtils.getItemWith2Categories2Tags();
        old.setUser(user);

        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        itemDTO.setName("Updated item name");
        itemDTO.setDescription("Updated item description");

        when(mockItemRepository.fetchFullDataById(any())).thenReturn(Optional.of(old));
        when(mockAddItemService.saveItem(any())).thenAnswer((Answer<Item>) invocation -> invocation.getArgument(0));

        ItemDTO updated = itemService.updateItem(itemDTO, usersMap.get(Authority.AUTHOR));

        assertEquals(old.getId(), updated.getId());
        assertEquals(itemDTO.getName(), updated.getName());
        assertEquals(itemDTO.getDescription(), updated.getDescription());
        assertEquals(2, updated.getCategories().size());
        assertEquals(2, updated.getTags().size());
    }

    @Test
    void updateItemNotByItemOwnerTest_ExpectException() {
        UserEntity user = usersMap.get(Authority.AUTHOR);
        Item old = ItemServiceTestUtils.get10ApprovesItems().get(0);
        old.setUser(user);

        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();
        itemDTO.setName("Updated item name");
        itemDTO.setDescription("Updated item description");

        when(mockItemRepository.fetchFullDataById(any())).thenReturn(Optional.of(old));

        assertThrows(HttpUnauthorizedException.class,
                () -> itemService.updateItem(itemDTO, usersMap.get(Authority.ADMIN)));
    }
    @Test
    void updateNonExistingItemTest_ExpectEntityNotFoundException() {

        ItemDTO itemDTO = ItemServiceTestUtils.getItemDTO();

        when(mockItemRepository.fetchFullDataById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(itemDTO, usersMap.get(Authority.SUPER_ADMIN)));
    }

    @Test
    void approveItemsTest(){
        Item item = ItemServiceTestUtils.getItemWith2Categories2Tags();

        when(mockItemRepository.existsById(1L)).thenReturn(true);
        when(mockItemRepository.fetchById(1L)).thenAnswer((Answer<Optional<Item>>) invocation -> {
            item.setId(1L);
            item.setApproved(true);
            return Optional.of(item);
        });

       // itemService.approve(1L);
      //  verify(mockItemRepository).approve(1L);
      //  assertTrue(item.isApproved());
    }

    @Test
    void approveNonExistingItemTest_ExpectThrow(){
        when(mockItemRepository.existsById(1L)).thenReturn(false);

       // assertThrows(EntityNotFoundException.class, () -> itemService.approve(1L));
    }

    @Test
    void getItemTest_ExpectSuccess(){
        Item item = ItemServiceTestUtils.getItemWith2Categories2Tags();
        when(mockItemRepository.fetchById(1L)).thenReturn(Optional.of(item));

        ItemDTO itemDTO = itemService.getItem(1L);

        assertEquals(item.getName(), itemDTO.getName());
        assertEquals(item.getDescription(), itemDTO.getDescription());
        assertEquals(item.getNotes(), itemDTO.getNotes());
        assertEquals(item.getId(), itemDTO.getId());
        assertEquals(item.getLink(), itemDTO.getLink());
        assertEquals(item.getCategories().size(), itemDTO.getCategories().size());
        assertEquals(item.getTags().size(), itemDTO.getTags().size());
    }

    @Test
    void getNonExistingItemTest_ExpectThrow(){
        when(mockItemRepository.fetchById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(1L));
    }

    @Test
    void getApprovedItemTest_ExpectSuccess(){
        Item item = ItemServiceTestUtils.getItemWith2Categories2Tags();
        item.setApproved(true);
        when(mockItemRepository.fetchById(1L)).thenReturn(Optional.of(item));

        ItemDTO itemDTO = itemService.getApprovedById(1L);

        assertEquals(item.getName(), itemDTO.getName());
        assertEquals(item.getDescription(), itemDTO.getDescription());
        assertEquals(item.getNotes(), itemDTO.getNotes());
        assertEquals(item.getId(), itemDTO.getId());
        assertEquals(item.getLink(), itemDTO.getLink());
        assertEquals(item.getCategories().size(), itemDTO.getCategories().size());
        assertEquals(item.getTags().size(), itemDTO.getTags().size());
    }

    @Test
    void getNonExistingApprovedItemTest_ExpectThrow(){

        when(mockItemRepository.fetchById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getApprovedById(1L));
    }

    @Test
    void getUnapprovedItemTest_ExpectThrow(){
        Item item = ItemServiceTestUtils.getItemWith2Categories2Tags();
        item.setApproved(false);
        when(mockItemRepository.fetchById(1L)).thenReturn(Optional.of(item));

        assertThrows(HttpUnauthorizedException.class, () -> itemService.getApprovedById(1L));
    }


}