package com.template.item.models;

import lombok.Getter;

import java.util.Set;

@Getter
public class ApproveItemResponse {

    private boolean error = false;
    private String message = "";
    private Set<ItemDTO> items;

    public ApproveItemResponse setError(boolean error) {
        this.error = error;
        return this;
    }

    public ApproveItemResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public ApproveItemResponse setItems(Set<ItemDTO> items) {
        this.items = items;
        return this;
    }
}
