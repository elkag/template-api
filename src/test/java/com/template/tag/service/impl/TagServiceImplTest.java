package com.template.tag.service.impl;

import com.template.category.entity.Category;
import com.template.tag.entity.Tag;
import com.template.tag.entity.TagsRepository;
import com.template.tag.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    private TagService tagService;
    @Mock
    private TagsRepository mockTagRepository;

    Tag unexistingTag;
    Tag existingTag;

    @BeforeEach
    public void setup(){
        tagService = new TagServiceImpl(mockTagRepository);

        unexistingTag = new Tag();
        unexistingTag.setName("Unexisting tag");

        existingTag = new Tag();
        existingTag.setName("Existing tag");
        existingTag.setId(1L);

        when(mockTagRepository.findByName(any())).thenAnswer((Answer<Optional<Tag>>) invocation -> {
            if(invocation.getArgument(0).equals(unexistingTag.getName())){
                return Optional.empty();
            }

            return Optional.of(existingTag);
        });
    }

    @Test
    void getOrSaveUnexcitingCategory_ExpectCategoryEntity() {

        when(mockTagRepository.save(any())).thenAnswer((Answer<Tag>) invocation -> {
            Tag saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        Tag saved = tagService.getOrSaveByName(unexistingTag.getName());

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(unexistingTag.getName(), saved.getName());
    }

    @Test
    void getOrSaveExcitingCategory_ExpectCategoryEntity() {
        Tag saved = tagService.getOrSaveByName(existingTag.getName());

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(existingTag, saved);
    }
}