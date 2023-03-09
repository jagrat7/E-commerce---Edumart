package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"id", "username", "password", "name", "phone", "home_address", "shipping_address", "email"})
public class UserDetails {
    @JsonProperty("id") private int id;
    @JsonProperty("username") private String username;
    @JsonProperty("password") private String password;
    @JsonProperty("name") private String name;
    @JsonProperty("phone") private long phone;
    @JsonProperty("home_address") private String home_address;
    @JsonProperty("shipping_address") private String shipping_address;
    @JsonProperty("email") private String email;
    private User_Customer[] customer;
    private User_Author[] author;
    private User_Admin[] admin;

    public UserDetails(int id, String username, String password, String name, long phone, String home_address, String shipping_address, String email){
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.home_address = home_address;
        this.shipping_address = shipping_address;
        this.email = email;
    }

    public UserDetails(int id){
        this.id = id;
    }

    public UserDetails(){

    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User_Customer[] getCustomer() {
        return customer;
    }

    public void setCustomer(User_Customer[] customer) {
        this.customer = customer;
    }

    public User_Author[] getAuthor() {
        return author;
    }

    public void setAuthor(User_Author[] author) {
        this.author = author;
    }

    public User_Admin[] getAdmin() {
        return admin;
    }

    public void setAdmin(User_Admin[] admin) {
        this.admin = admin;
    }
}
