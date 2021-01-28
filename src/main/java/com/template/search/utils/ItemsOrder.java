package com.template.search.utils;

import lombok.Getter;

@Getter
public enum ItemsOrder {
    NAME("name"),
    DATE("creationDate"),
    USER(""),
    APPROVED("isApproved");

    private final String orderBy;

    ItemsOrder(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
