package com.estore.api.estoreapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Represents a Product entity
 *
 * @author Poorna Chander (pp5109@rit.edu)
 */

@JsonIgnoreProperties(value = {"id", "name", "description", "price", "topic", "rating", "review", "author", "image", "status", "toBeDeleted", "createdBy", "liveData", "toBeAdded"})
public class Product {
    public enum Status{
        IN_LIVE,
        TO_BE_ADDED,
        TO_BE_DELETED
    }
    
    @JsonProperty("id") private int id;
    @JsonProperty("name") private String name;
    @JsonProperty("description") private String description;
    @JsonProperty("price") private float price;
    @JsonProperty("topic") private List<String> topic ;
    @JsonProperty("rating") private float rating;
    @JsonProperty("review") private List<String> review;
    @JsonProperty("author") private String author;
    @JsonProperty("image") private Image image;
    @JsonProperty("status") private Status status = Status.IN_LIVE;
    @JsonProperty("createdBy") private int createdBy;

    private Video[] videos;
    private Book[] books;
    private Audio[] audios;

    public Product(int id, String name, String description, float price, List<String> topic, float rating, List<String> review, String author,  Image image, Status status, int createdBy){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.topic = topic;
        this.rating = rating;
        this.review = review;
        this.author = author;
        this.image = image;
        this.status = status;
        this.createdBy = createdBy;
    }

    public Product(int id){
        this.id = id;
    }

    public Product(){
        this.videos = new Video[0];
        this.books = new Book[0];
        this.audios = new Audio[0];
    }

    public Video[] getVideos() {
        return videos;
    }


    public void setVideos(Video[] videos) {
        this.videos = videos;
    }


    public Book[] getBooks() {
        return books;
    }


    public void setBooks(Book[] books) {
        this.books = books;
    }


    public Audio[] getAudios() {
        return audios;
    }


    public void setAudios(Audio[] audios) {
        this.audios = audios;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public List<String> getTopic() {
        return topic;
    }

    public float getRating() {
        return rating;
    }

    public List<String> getReview() {
        return review;
    }

    public String getAuthor() {
        return author;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean isToBeDeleted(){
        return status == Status.TO_BE_DELETED;
    }

    public Boolean isLiveData(){
        return status == Status.IN_LIVE;
    }

    public Boolean isToBeAdded(){
        return status == Status.TO_BE_ADDED;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image{
        public enum imageType{
            URL,
            LOCAL
        }
        @JsonProperty("type") private imageType type;
        @JsonProperty("imageSrc") private List<String> imageSrc;

        public Image(@JsonProperty("type") imageType type,
                    @JsonProperty("imageSrc") List<String> imageSrc){
            this.type = type;
            this.imageSrc = imageSrc;
        }

        public imageType getType() {
            return type;
        }
        public void setType(imageType type) {
            this.type = type;
        }
        public List<String> getImageSrc() {
            return imageSrc;
        }
        public void setImageSrc(List<String> imageSrc) {
            this.imageSrc = imageSrc;
        }
    }
}
