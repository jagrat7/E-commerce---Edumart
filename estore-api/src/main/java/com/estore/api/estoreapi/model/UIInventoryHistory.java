package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"fieldChanges"})
public class UIInventoryHistory {
    private InventoryHistoryActions[] actions;
    private long timeStamp;

    public UIInventoryHistory(InventoryHistoryActions[] actions, long timeStamp){
        this.actions = actions;
        this.timeStamp = timeStamp;
    }

    public InventoryHistoryActions[] getActions() {
        return actions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
