package com.estore.api.estoreapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * WIP for product specialization level (Books) data handling in sprint 2
 *
 * @author poorna chander (pp5109)
 */
@JsonIgnoreProperties(value = {"videos", "books","audios", "toBeDeleted", "liveData", "toBeAdded"})
public class Book extends Product{

    @JsonProperty("bind_type") private String bindType;
    @JsonProperty("print_type") private List<String> printType;
    @JsonProperty("pages") private int pages;
    @JsonProperty("weight") private float weight;
    @JsonProperty("quantity") private int quantity;

    public Book(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("price") float price,
                @JsonProperty("topic") List<String> topic,
                @JsonProperty("rating") float rating,
                @JsonProperty("review") List<String> review,
                @JsonProperty("author") String author,
                @JsonProperty("image") Image image,
                @JsonProperty("status") Status status,
                @JsonProperty("createdBy") int createdBy,
                @JsonProperty("bind_type") String bindType,
                @JsonProperty("print_type") List<String> printType,
                @JsonProperty("pages") int pages,
                @JsonProperty("weight") float weight,
                @JsonProperty("quantity") int quantity
                ) {
     super(id, name, description, price, topic, rating, review, author, image, status, createdBy);
     this.bindType = bindType;
     this.printType = printType;
     this.pages = pages;
     this.weight = weight;
     this.quantity = quantity;
    }

    public Book(int id){
        super(id);
    }

    // public int getId() {
    //     return super.getId();
    // }

    // public String getName() {
    //     return super.getName();
    // }

    // public String getDescription() {
    //     return super.getDescription();
    // }

    // public float getPrice() {
    //     return super.getPrice();
    // }

    // public List<String> getTopic() {
    //     return super.getTopic();
    // }

    // public float getRating() {
    //     return super.getRating();
    // }

    // public List<String> getReview() {
    //     return super.getReview();
    // }

    // public String getAuthor() {
    //     return super.getAuthor();
    // }
    
    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public List<String> getPrintType() {
        return printType;
    }

    public void setPrintType(List<String> printType) {
        this.printType = printType;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
