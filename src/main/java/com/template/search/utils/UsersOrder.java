package com.template.search.utils;

import lombok.Getter;

@Getter
public enum UsersOrder {
    USERNAME("username"),
    DATE("registrationDate"),
    NAME(""),
    ADMIN("isAdmin");

    private final String orderBy;

    UsersOrder(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
