package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"userId", "timeStamp"})
public class History {
    @JsonProperty("userId") private int userId;
    @JsonProperty("timeStamp") private long timeStamp;

    @JsonProperty("purchase")
    private PurchaseHistory[] purchase;

    @JsonProperty("inventory")
    private InventoryHistory[] inventory;

    public History(){
        
    }
    public History(int userId, long timeStamp){
        this.userId = userId;
        this.timeStamp = timeStamp;
    }

    public PurchaseHistory[] getPurchase() {
        return purchase;
    }

    public void setPurchase(PurchaseHistory[] purchase) {
        this.purchase = purchase;
    }

    public InventoryHistory[] getInventory() {
        return inventory;
    }

    public void setInventory(InventoryHistory[] inventory) {
        this.inventory = inventory;
    }

    public int getUserId() {
        return userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
