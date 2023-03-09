package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cart {
    @JsonProperty("user_id")
    private int user_id;
    @JsonProperty("products")
    private CartProducts[] products;
    @JsonProperty("sessions")
    private CartSessions[] sessions;

    public Cart(@JsonProperty("user_id") int user_id,
            @JsonProperty("products") CartProducts[] products,
            @JsonProperty("sessions") CartSessions[] sessions) {
        this.user_id = user_id;
        this.products = products;
        this.sessions = sessions;
    }

    public int getUser_id() {
        return user_id;
    }

    public CartProducts[] getProducts() {
        return products;
    }

    public CartSessions[] getSessions() {
        return sessions;
    }

    public void setProducts(CartProducts[] products) {
        this.products = products;
    }

    public void setSessions(CartSessions[] sessions) {
        this.sessions = sessions;
    }
}
