package com.template.item.models;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SearchResultDTO {
    private final int pages;
    private final List<ItemDTO> result;
}
