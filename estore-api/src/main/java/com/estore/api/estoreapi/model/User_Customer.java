package com.estore.api.estoreapi.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"customer", "author", "admin"})
public class User_Customer extends UserDetails{
    @JsonProperty("payment_type") private String payment_type;
    @JsonProperty("card_details") private HashMap<String,Object> card_details;
    @JsonProperty("wallet_details") private HashMap<String,Object> wallet_details;

    public User_Customer(@JsonProperty("id") int id,
    @JsonProperty("username") String username,
    @JsonProperty("password") String password,
    @JsonProperty("name") String name,
    @JsonProperty("phone") long phone,
    @JsonProperty("home_address") String home_address,
    @JsonProperty("shipping_address") String shipping_address,
    @JsonProperty("email") String email,
    @JsonProperty("payment_type") String payment_type,
    @JsonProperty("card_details") HashMap<String,Object> card_details,
    @JsonProperty("wallet_details") HashMap<String,Object> wallet_details){
        super(id, username, password, name, phone, home_address, shipping_address, email);
        this.payment_type = payment_type;
        this.card_details = card_details;
        this.wallet_details = wallet_details;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public HashMap<String, Object> getCard_details() {
        return card_details;
    }

    public HashMap<String, Object> getWallet_details() {
        return wallet_details;
    }
}
