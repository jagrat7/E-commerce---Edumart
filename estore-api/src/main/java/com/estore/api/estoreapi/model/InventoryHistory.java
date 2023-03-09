package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"inventory", "purchase"})
public class InventoryHistory extends History{
    @JsonProperty("actions") private InventoryHistoryActions[] actions;

    public InventoryHistory(@JsonProperty("userId") int userId,
                             @JsonProperty("timeStamp") long timeStamp) {
        super(userId, timeStamp);
    }
    
    public InventoryHistoryActions[] getActions() {
        return actions;
    }

    public void setActions(InventoryHistoryActions[] actions) {
        this.actions = actions;
    }
    
}
