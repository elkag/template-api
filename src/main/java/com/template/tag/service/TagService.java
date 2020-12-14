package com.template.tag.service;

import com.template.tag.entity.Tag;

public interface TagService {
    Tag getOrSaveByName(String name);
}
