package com.estore.api.estoreapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"videos", "books","audios", "toBeDeleted", "liveData", "toBeAdded"})
public class Audio extends Product{

    @JsonProperty("duration") private long duration;
    @JsonProperty("format") private List<String> format;

    public Audio(@JsonProperty("id") int id,
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
                @JsonProperty("duration") long duration,
                @JsonProperty("format") List<String> format
                ) {
     super(id, name, description, price, topic, rating, review, author, image, status, createdBy);
     this.duration = duration;
     this.format = format;
    }

    public Audio(int id){
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
    
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<String> getFormat() {
        return format;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }

}

