package com.template.search.utils;

import lombok.Getter;

@Getter
public enum OrderDirection {
    ASC("asc"),
    DESC("desc");

    private final String direction;

    OrderDirection(String direction) {
        this.direction = direction;
    }
}
