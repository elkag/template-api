package com.template.item.utils;

import com.template.category.entity.Category;
import com.template.item.entities.Item;
import com.template.item.models.ItemDTO;
import com.template.tag.entity.Tag;
import com.template.user.entities.Authority;
import com.template.user.entities.AuthorityEntity;
import com.template.user.entities.UserEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ItemServiceTestUtils {

    public static Map<Authority, UserEntity> getUsers(){
        Map<Authority, UserEntity> usersMap = new HashMap<>();
        usersMap.put(Authority.SUPER_ADMIN, createSuperAdmin());
        usersMap.put(Authority.ADMIN, createAdmin());
        usersMap.put(Authority.AUTHOR, createAuthor());
        usersMap.put(Authority.USER, createUser());
        return usersMap;
    }

    public static ItemDTO getItemDTO(){
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName("Item 1");
        itemDTO.setLink("http://link.to");
        itemDTO.setDescription("Item description 1");
        itemDTO.setNotes("Item notes 1");
        itemDTO.setCategories(Set.of("cat 1", "cat 2"));
        itemDTO.setTags(Set.of("tag 1", "tag 2"));
        return itemDTO;
    }

    public static Item getItemWith2Categories2Tags() {
        Item item = new Item();
        item.setName("Item name");
        item.setDescription("Item description");
        item.setLink("http://link.url");
        item.setCategories(Set.of(new Category("cat 1"), new Category("cat 2")));
        item.setTags(Set.of(new Tag("tag 1"), new Tag("tag 2")));

        return item;
    }

    public static List<Item> get10ApprovesItems(){
        return IntStream.range(0, 10).mapToObj(i -> {
            Item item = new Item();
            item.setId(i + 1L);
            item.setName("Item " + i);
            item.setDescription("Item description " + i);
            item.setNotes("Item notes " + i);
            item.setLink("http://link.url");
            item.setApproved(true);
            item.setCategories(Set.of(new Category("cat " + i)));
            item.setTags(Set.of(new Tag("tag " + i)));
            return item;
        }).collect(Collectors.toList());
    }

    public static List<Item> get10UnapprovesItems(){
        return IntStream.range(0, 10).mapToObj(i -> {
            Item item = new Item();
            item.setId(i + 1L);
            item.setName("Item " + i);
            item.setDescription("Item description " + i);
            item.setNotes("Item notes " + i);
            item.setLink("http://link.url");
            item.setApproved(false);
            item.setCategories(Set.of(new Category("cat " + i)));
            item.setTags(Set.of(new Tag("tag " + i)));
            return item;
        }).collect(Collectors.toList());
    }

    private static UserEntity createSuperAdmin(){
        UserEntity superAdmin = new UserEntity();
        superAdmin.setId(1L);
        superAdmin.setUsername("super-admin");
        superAdmin.setPassword("super-admin");
        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");

        superAdmin.setRoles(
                new AuthorityEntity(Authority.SUPER_ADMIN.name()),
                new AuthorityEntity(Authority.ADMIN.name()),
                new AuthorityEntity(Authority.AUTHOR.name()),
                new AuthorityEntity(Authority.USER.name()));
        return superAdmin;
    }

    private static UserEntity createAdmin(){
        UserEntity admin = new UserEntity();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");

        admin.setRoles(
                new AuthorityEntity(Authority.ADMIN.name()),
                new AuthorityEntity(Authority.AUTHOR.name()),
                new AuthorityEntity(Authority.USER.name()));
        return admin;
    }

    private static UserEntity createAuthor(){
        UserEntity author = new UserEntity();
        author.setId(1L);
        author.setUsername("author");
        author.setPassword("author");
        author.setFirstName("Author");
        author.setLastName("Author");

        author.setRoles(
                new AuthorityEntity(Authority.AUTHOR.name()),
                new AuthorityEntity(Authority.USER.name()));
        return author;
    }

    private static UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("user");
        user.setFirstName("User");
        user.setLastName("User");

        user.setRoles(
                new AuthorityEntity(Authority.USER.name()));
        return user;
    }
}
