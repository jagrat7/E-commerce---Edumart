package com.estore.api.estoreapi.model;

public class UICartProducts {
    private int quantity;
    private String type;
    private Object productDetails;

    public UICartProducts(
        int quantity,
        String type,
        Object productDetails
    ){
        this.quantity = quantity;
        this.type = type;
        this.productDetails = productDetails;
    }

    
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public Object getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(Object productDetails) {
        this.productDetails = productDetails;
    }
    
}
