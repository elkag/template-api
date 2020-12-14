package com.template.category.service.impl;

import com.template.category.entity.Category;
import com.template.category.entity.CategoryRepository;
import com.template.category.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category getOrSaveByName(String name) {
        Optional<Category> categoryOpt = categoryRepository.findByName(name);
        if(categoryOpt.isEmpty()){
            return categoryRepository.save(new Category(name));
        }
        return categoryOpt.get();
    }
}
