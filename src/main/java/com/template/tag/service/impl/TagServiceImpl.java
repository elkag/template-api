package com.template.tag.service.impl;

import com.template.tag.entity.Tag;
import com.template.tag.entity.TagsRepository;
import com.template.tag.service.TagService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private final TagsRepository tagsRepository;

    public TagServiceImpl(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    @Override
    public Tag getOrSaveByName(String name) {
        Optional<Tag> tagOpt = tagsRepository.findByName(name);
        if(tagOpt.isEmpty()){
            return tagsRepository.save(new Tag(name));
        }
        return tagOpt.get();
    }
}
