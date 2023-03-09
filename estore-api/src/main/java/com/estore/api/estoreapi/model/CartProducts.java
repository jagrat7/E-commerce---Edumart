package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartProducts {
    @JsonProperty("type") private String type;
    @JsonProperty("id") private int id;
    @JsonProperty("quantity") private int quantity;

    public CartProducts(
        @JsonProperty("type") String type,
        @JsonProperty("id") int id,
        @JsonProperty("quantity") int quantity
    ){
        this.type = type;
        this.id = id;
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }
    public int getId() {
        return id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
