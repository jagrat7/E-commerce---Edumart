package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartSessions {
    @JsonProperty("name")
    private String name;
    @JsonProperty("time")
    private int time;
    @JsonProperty("status")
    private String status;

    public CartSessions(@JsonProperty("name") String name,
            @JsonProperty("time") int time,
            @JsonProperty("status") String status) {
        this.name = name;
        this.time = time;
        this.status = status;
    };

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
