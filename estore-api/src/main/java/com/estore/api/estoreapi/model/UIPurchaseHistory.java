package com.estore.api.estoreapi.model;

public class UIPurchaseHistory {
    private PurchaseHistoryProducts[] products;
    private UICartSessions[] sessions;
    private long timeStamp;

    public UIPurchaseHistory(PurchaseHistoryProducts[] products, UICartSessions[] sessions, long timeStamp){
        this.products = products;
        this.sessions = sessions;
        this.timeStamp = timeStamp;
    }

    public PurchaseHistoryProducts[] getProducts() {
        return products;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public UICartSessions[] getSessions() {
        return sessions;
    }

}
