package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"userId", "timeStamp"})
public class PurchaseHistoryProducts {
    @JsonProperty("type") private String type;
    @JsonProperty("id") private int id;
    @JsonProperty("name") private String name;
    @JsonProperty("quantity") private int quantity;

    public PurchaseHistoryProducts(
        @JsonProperty("type") String type,
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("quantity") int quantity
    ){
        this.type = type;
        this.id = id;
        this.name = name;
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
    public String getName(){
        return name;
    }
}
