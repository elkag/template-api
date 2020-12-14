package com.template.category.service.impl;

import com.template.category.entity.Category;
import com.template.category.entity.CategoryRepository;
import com.template.category.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    private CategoryService categoryService;
    @Mock
    private CategoryRepository mockCategoryRepository;

    Category nonExistingCategory;
    Category existingCategory;

    @BeforeEach
    public void setup(){
        categoryService = new CategoryServiceImpl(mockCategoryRepository);

        nonExistingCategory = new Category();
        nonExistingCategory.setName("Non existing category");

        existingCategory = new Category();
        existingCategory.setName("Existing category");
        existingCategory.setId(1L);

        when(mockCategoryRepository.findByName(any())).thenAnswer((Answer<Optional<Category>>) invocation -> {
            if(invocation.getArgument(0).equals(nonExistingCategory.getName())){
                return Optional.empty();
            }

            return Optional.of(existingCategory);
        });
    }

    @Test
    void getOrSaveNonExistingCategory_ExpectCategoryEntity() {

        when(mockCategoryRepository.save(any())).thenAnswer((Answer<Category>) invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        Category saved = categoryService.getOrSaveByName(nonExistingCategory.getName());

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(nonExistingCategory.getName(), saved.getName());
    }

    @Test
    void getOrSaveExcitingCategory_ExpectCategoryEntity() {
        Category saved = categoryService.getOrSaveByName(existingCategory.getName());

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(existingCategory, saved);
    }
}