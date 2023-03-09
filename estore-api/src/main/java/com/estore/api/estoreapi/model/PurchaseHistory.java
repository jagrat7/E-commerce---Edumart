package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"inventory", "purchase"})
public class PurchaseHistory extends History{
    @JsonProperty("products") private PurchaseHistoryProducts[] products;
    @JsonProperty("sessions") private UICartSessions[] sessions;

    public PurchaseHistory(@JsonProperty("userId") int userId, 
                            @JsonProperty("timeStamp") long timeStamp) {
        super(userId, timeStamp);
    }
    
    public PurchaseHistoryProducts[] getProducts() {
        return products;
    }

    public void setProducts(PurchaseHistoryProducts[] products) {
        this.products = products;
    }
    
    public UICartSessions[] getSessions() {
        return sessions;
    }

    public void setSessions(UICartSessions[] sessions) {
        this.sessions = sessions;
    }

}
