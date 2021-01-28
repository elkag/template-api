package com.template.item.models;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageDTO {
    private final long totalPages;
    private final long totalElements;
    private final List<ShortItemDTO> result;
}
