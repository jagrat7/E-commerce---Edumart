package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"customer", "author", "admin"})
public class User_Admin extends UserDetails {
    public User_Admin(@JsonProperty("id") int id,
    @JsonProperty("username") String username,
    @JsonProperty("password") String password,
    @JsonProperty("name") String name,
    @JsonProperty("phone") long phone,
    @JsonProperty("home_address") String home_address,
    @JsonProperty("shipping_address") String shipping_address,
    @JsonProperty("email") String email){
        super(id, username, password, name, phone, home_address, shipping_address, email);
    }
}
