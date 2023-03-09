package com.estore.api.estoreapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"customer", "author", "admin"})
public class User_Author extends UserDetails {
    @JsonProperty("rating") private int rating;
    @JsonProperty("reviews") private List<String> reviews;
    @JsonProperty("topics") private List<String> topics;
    @JsonProperty("grades") private List<String> grades;
    @JsonProperty("author_description") private String author_description;
    @JsonProperty("slots") private List<Long> slots;
    @JsonProperty("link") private String link;
    @JsonProperty("platform") private String platform;

    public User_Author(@JsonProperty("id") int id,
    @JsonProperty("username") String username,
    @JsonProperty("password") String password,
    @JsonProperty("name") String name,
    @JsonProperty("phone") long phone,
    @JsonProperty("home_address") String home_address,
    @JsonProperty("shipping_address") String shipping_address,
    @JsonProperty("email") String email, 
    @JsonProperty("rating") int rating,
    @JsonProperty("reviews") List<String> reviews,
    @JsonProperty("topics") List<String> topics,
    @JsonProperty("grades") List<String> grades,
    @JsonProperty("author_description") String author_description,
    @JsonProperty("slots") List<Long> slots,
    @JsonProperty("link") String link,
    @JsonProperty("platform") String platform){
      super(id, username, password, name, phone, home_address, shipping_address, email);
      this.rating = rating;
      this.reviews = reviews;
      this.topics = topics;
      this.grades = grades;
      this.author_description = author_description;
      this.slots = slots;
      this.link = link;
      this.platform = platform;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public List<String> getTopics() {
        return topics;
    }

    public List<String> getGrades() {
        return grades;
    }

    public String getAuthor_description() {
        return author_description;
    }

    public List<Long> getSlots() {
        return slots;
    }

    public String getLink() {
        return link;
    }

    public String getPlatform() {
        return platform;
    }
 
}
