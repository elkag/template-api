package com.template.item.mappers;

import com.template.category.entity.Category;
import com.template.item.entities.Item;
import com.template.item.models.ItemDTO;
import com.template.tag.entity.Tag;
import com.template.user.entities.Authority;
import com.template.user.entities.AuthorityEntity;
import com.template.user.mappers.UserMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public abstract class ItemMapper {
    public static final ItemMapper INSTANCE = Mappers.getMapper( ItemMapper.class );

    public abstract ItemDTO toItemDTO(Item item);

    public abstract Set<ItemDTO> toItemDTOs(Set<Item> items);

    public abstract Item toItem(ItemDTO model);

    @Named("mapToString")
    protected String categoryToString(Category category){
        return category.getName();
    }

    @IterableMapping(qualifiedByName = "mapToString")
    protected abstract Set<String> stringsToCategories(Set<Category> categories);

    @Named("mapToCategory")
    protected Category stringToCategory(String catString){
        return new Category(catString);
    }

    @IterableMapping(qualifiedByName = "mapToCategory")
    protected abstract Set<Category> categoriesToStrings(Set<String> categories);

    // Tags mapping
    @Named("tagToString")
    protected String categoryToString(Tag tag){
        return tag.getName();
    }

    @IterableMapping(qualifiedByName = "tagToString")
    protected abstract Set<String> stringsToTags(Set<Tag> tag);

    @Named("mapToTag")
    protected Tag stringToTag(String tagName){
        return new Tag(tagName);
    }

    @IterableMapping(qualifiedByName = "mapToTag")
    protected abstract Set<Tag> tagsToStrings(Set<String> tags);


}
