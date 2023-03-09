package com.estore.api.estoreapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WIP for product specialization level (videos) data handling in sprint 2
 *
 * @author poorna chander (pp5109)
 */
@JsonIgnoreProperties(value = {"videos", "books","audios", "toBeDeleted", "liveData", "toBeAdded"})
public class Video extends Product{

    @JsonProperty("duration") private long duration;
    @JsonProperty("resolution") private List<String> resolution;
    @JsonProperty("format") private List<String> format;

    public Video(@JsonProperty("id") int id,
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
                @JsonProperty("resolution") List<String> resolution,
                @JsonProperty("format") List<String> format
                ) {
     super(id, name, description, price, topic, rating, review, author, image, status, createdBy);
     this.duration = duration;
     this.resolution = resolution;
     this.format = format;
    }

    public Video(int id){
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

    public List<String> getResolution() {
        return resolution;
    }

    public void setResolution(List<String> resolution) {
        this.resolution = resolution;
    }

    public List<String> getFormat() {
        return format;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }

}

