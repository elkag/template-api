package com.template.category.service;

import com.template.category.entity.Category;

public interface CategoryService {

    Category getOrSaveByName(String name);
}
