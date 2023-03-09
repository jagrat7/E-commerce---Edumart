package com.estore.api.estoreapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.estore.api.estoreapi.model.Audio;
import com.estore.api.estoreapi.model.Book;
import com.estore.api.estoreapi.model.Product;
import com.estore.api.estoreapi.model.Product.Image;
import com.estore.api.estoreapi.model.Product.Image.imageType;
import com.estore.api.estoreapi.model.Product.Status;
import com.estore.api.estoreapi.model.UserDetails;
import com.estore.api.estoreapi.model.User_Admin;
import com.estore.api.estoreapi.model.User_Author;
import com.estore.api.estoreapi.model.User_Customer;
import com.estore.api.estoreapi.model.Video;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProductFileDAOTest {
    private Product dummyProduct;
    private ProductFileDAO productFileDAO;
    ObjectMapper mockObjectMapper;

    @BeforeEach
    public void setupproductFileDAO() throws StreamWriteException, DatabindException, IOException {
        Random random = new Random();
        List<String> allowedtypesmock = new ArrayList<String>();
        allowedtypesmock.add("video");
        allowedtypesmock.add("audio");
        allowedtypesmock.add("book");
        dummyProduct = getDummyProduct(random.nextInt(6, 10), random.nextInt(6, 10), random.nextInt(6, 10));
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        // objectMapper.writeValue(new File("test.json"), dummyProduct);
        when(objectMapper.readValue(new File("test.json"), Product.class)).thenReturn(dummyProduct);
        try {
            productFileDAO = new ProductFileDAO("test.json", allowedtypesmock, objectMapper);
        } catch (IOException e) {
            // Auto generated catch block
            e.printStackTrace();
        }
    }

    private Product getDummyProduct(int videoSize, int audioSize, int bookSize){
        Video[] videos;
        Audio[] audios;
        Book[] books;

        //Video Generation
        List<String> topics = Arrays.asList("Summer_Coach", "Thermodynamics", "SWEN-601", "Drivers", "SWEN-602", "SWEN-610", "SWEN-746", "What is a Topic?", "Bikes", "Cars", "Trucks", "Algebra");
        List<String> review = Arrays.asList("He is good", "bad", "Too Young", "Too Old", "Is he a Professor?", "Rocks", "Excellent", "En Saami!", "Super Man", "Devil", "Clown", "Fire!");
        List<String> resolution = Arrays.asList("2160", "1080", "720", "480", "360", "240");
        List<String> format = Arrays.asList("mp4", "avi", "quickmov", "wmv", "mov", "flv");
        
        List<Video> videosList = new ArrayList<Video>();

        int videoId = 1;
        String name = "Fired";
        String description = "Description";
        float price = 200;
        float rating = 0.5f;
        String author = "Mark";
        long duration = 2000;

        Random random = new Random();

        List<String> imageSrc = new ArrayList<>();
        imageSrc.add("https://www.google.com");
        Image image = new Image(imageType.URL, imageSrc);

        for(int i = 0; i < videoSize; i++){
            Video video = new Video(videoId++, name + ( i + 1 ), description + ( i + 1 ), price + ( i * 2 ), getRandomSubList(topics), rating + ( 0.5f ), getRandomSubList(review), author + ( i + 1 ), image, Status.IN_LIVE, getDummyUserDetails(1).getId(), duration + 2000, getRandomSubList(resolution), getRandomSubList(format));
            videosList.add(video);
        }
        videos = new Video[videosList.size()];
        videosList.toArray(videos);
        
        //Audio Generation   
        List<Audio> audiosList = new ArrayList<Audio>();

        int audioId = 1;

        for(int i = 0; i < audioSize; i++){
            Audio audio = new Audio(audioId++, name + ( i + 1 ), description + ( i + 1 ), price + ( i * 2 ), getRandomSubList(topics), rating + ( 0.5f ), getRandomSubList(review), author + ( i + 1 ), image, Status.IN_LIVE, getDummyUserDetails(1).getId(), duration + 2000, getRandomSubList(resolution));
            audiosList.add(audio);
        }
        audios = new Audio[audiosList.size()];
        audiosList.toArray(audios);

        //Book Generation   
        List<String> bindType = Arrays.asList("hard", "soft");
        List<String> print_type = Arrays.asList("pdf", "docx", "xml", "document");
        int pages = 100;
        float weight = 100;
        int quantity = 10;
        List<Book> booksList = new ArrayList<Book>();

        int bookId = 1;

        for(int i = 0; i < bookSize; i++){
            Book book = new Book(bookId++, name + ( i + 1 ), description + ( i + 1 ), price + ( i * 2 ), getRandomSubList(topics), rating + ( 0.5f ), getRandomSubList(review), author + ( i + 1 ), image, Status.IN_LIVE, getDummyUserDetails(1).getId(), bindType.get(random.nextInt(0,1)), getRandomSubList(print_type), pages + ( 50 ), weight + ( 50 ), quantity + ( 10 ));
            booksList.add(book);
        }
        books = new Book[booksList.size()];
        booksList.toArray(books);

        Product product = new Product();
        product.setAudios(audios);
        product.setBooks(books);
        product.setVideos(videos);
        return product;
    }

    private UserDetails getDummyUserDetails(){
        UserDetails userDtls = new UserDetails(1, "dummy", "dummy", "dummy", 5152459812l, "dummy", "dummy", "dummy@gmail.com");
        HashMap<String,Object> wallet_details = new HashMap<String,Object>();
        wallet_details.put("id", 22333);
        userDtls.setCustomer(new User_Customer[] {new User_Customer(userDtls.getId(), userDtls.getUsername(), userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), "wallet", null, wallet_details)});
        return userDtls;
    }

    public UserDetails getDummyUserDetails(int type){
        int nextUserId = 1;
        switch(type){
            case 1:
            User_Admin admin = new User_Admin(nextUserId, "dummy_admin", "dummy_admin", "dummy_admin", 12345, "dummy", "dummy", "dummy@gmail.com");
            return admin;
            case 2:
            User_Author author = new User_Author(nextUserId, "dummy_author", "dummy_author", "dummy_author", 67890, "dummy", "dummy", "dummy@gmail.com", 5, Arrays.asList("Good","Bad"), Arrays.asList("Thermo","Machine"), Arrays.asList("G1","G2"), "Good one is me", Arrays.asList(1212341243124l,5346534654l), "zoom.us/dummy", "zoom");
            return author;
            default:;
            HashMap<String,Object> wallet = new HashMap<String,Object>();
            wallet.put("id", 236491);
            User_Customer customer = new User_Customer(nextUserId, "dummy_author", "dummy_author", "dummy_author", 67890, "dummy", "dummy", "dummy@gmail.com", "wallet", new HashMap<String,Object>(), wallet);
            return customer;
        }
    }

    public <T> List<T> getRandomSubList(List<T> list) {
        Random random = new Random();
        int startIndex = random.nextInt(0, list.size());
        int endIndex = random.nextInt(startIndex, list.size());
        return list != null ? list.subList(startIndex, endIndex) : null;
    }

    @Test
    public void testGetProducts() throws IOException {
        // Invoke
        Product product = productFileDAO.getProducts();
        // Analyze
        assertEquals(dummyProduct.getVideos().length, product.getVideos().length);
        assertEquals(dummyProduct.getBooks().length, product.getBooks().length);
        assertEquals(dummyProduct.getAudios().length, product.getAudios().length);
    }

    @Test
    public void testFindProducts() throws IOException {
        // Setup
        String searchString = "Fired1";
        // Invoke
        Product product = productFileDAO.findProducts(searchString);
        // Analyze
        assertTrue(product.getBooks().length > 0);
    }

    @Test
    public void testFindProducts_ProductNotFound() throws IOException {
        // Setup
        String searchString = "Jacoco";

        // Invoke
        Product product = productFileDAO.findProducts(searchString);

        // Analyze
        assertTrue(product.getBooks().length == 0);
    }

    @Test
    public void testGetProduct() throws Exception {
        // Setup
        Video video = dummyProduct.getVideos()[0];
        int id = video.getId();
        String type = "video";
        Video[] singlevideolist = { video };

        Product emptyProduct = getDummyProduct(0, 0, 0);
        emptyProduct.setVideos(singlevideolist);

        // Invoke
        Product product = productFileDAO.getProduct(id, type);
        // Analyze
        assertEquals(emptyProduct.getVideos()[0], product.getVideos()[0]);
        // Or assertTrue(product.getBooks().length > 0);
    }

    // @Test
    // public void testGetProduct_TypeNotFound() throws Exception {
    // // Setup
    // int id = 1;
    // String type = "strangeType";
    // // Invoke
    // Product product = productFileDAO.getProduct(id, type);
    // // Analyze
    // // Should assert throw exception here, but how?
    // assertNull(product);
    // }

    @Test
    public void testGetProduct_ProductNotFound() throws Exception {
        // Setup
        int id = 100;
        String type = "book";
        // Invoke
        Product product = productFileDAO.getProduct(id, type);
        // Analyze
        assertNull(product);
    }

    @Test
    public void testGetVideo() throws IOException {
        // Setup
        Video expected_video = dummyProduct.getVideos()[0];
        int id = expected_video.getId();
        // Invoke
        Video video = productFileDAO.getVideo(id);
        // Analyze
        assertEquals(expected_video, video);
    }

    @Test
    public void testGetVideo_VideoNotFound() throws IOException {
        // Setup
        int id = 100;
        // Invoke
        Video video = productFileDAO.getVideo(id);
        // Analyze
        assertNull(video);
    }

    @Test
    public void testGetBook() throws IOException {
        // Setup
        Book expected_book = dummyProduct.getBooks()[0];
        int id = expected_book.getId();
        // Invoke
        Book book = productFileDAO.getBook(id);
        // Analyze
        assertEquals(dummyProduct.getBooks()[0], book);

    }

    @Test
    public void testGetBook_BookNotFound() throws IOException {
        // Setup
        int id = 500;
        // Invoke
        Book book = productFileDAO.getBook(id);
        // Analyze
        assertNull(book);
    }

    @Test
    public void testGetAudio() throws IOException {
        // Setup
        Audio expected_audio = dummyProduct.getAudios()[0];
        int id = expected_audio.getId();
        // Invoke
        Audio audio = productFileDAO.getAudio(id);
        // Analyze
        assertEquals(expected_audio, audio);
    }

    @Test
    public void testGetAudio_AudioNotFound() throws IOException {
        // Setup
        int id = 100;
        // Invoke
        Audio audio = productFileDAO.getAudio(id);
        // Analyze
        assertNull(audio);
    }

    @Test
    public void testUpdateProduct() throws Exception {

        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Setup
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0],
                new TypeReference<HashMap<String, Object>>() {
                });
        map.put("name", map.get("name") + "_updated");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);
        map.put("type", "video");
        map.remove("image");
        String a = map.get("name") + "";
        // Video expectedVideo = mapper.convertValue(map, Video.class);
        // Product expectedProduct = new Product();
        // expectedProduct.setVideos(new Video[] { expectedVideo });

        // Invoke
        Product updatedProduct = productFileDAO.updateProduct(map);

        // Analyze
        assertEquals(updatedProduct.getVideos()[0].getName(), a);
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Setup
        Book expected_book = dummyProduct.getBooks()[0];
        int id = expected_book.getId();
        // Invoke
        boolean actual = productFileDAO.deleteProduct(id, "book");
        // Analyze
        assertTrue(actual);
    }

    @Test
    public void testDeleteProduct_ProductNotFound() throws Exception {
        // Setup
        int id = 100;
        String type = "book";
        // Invoke
        boolean actual = productFileDAO.deleteProduct(id, type);
        // Analyze
        assertFalse(actual);
    }

    @Test
    public void testCreateProduct() throws Exception {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0],
                new TypeReference<HashMap<String, Object>>() {
                });
        // when(mockProductDAO.createProduct(map)).thenReturn(product.getVideos()[0]);
        map.put("name", map.get("name") + "_created");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);
        map.put("type", "video");
        map.remove("image");
        String a = map.get("name") + "";

        Product createdProduct = productFileDAO.createProduct(map);

        // Analyze
        assertEquals(createdProduct.getVideos()[0].getName(), a);
    }

    // @Test
    // public void testCreateProduct_TypeNotFound() throws IOException {
    // // Setup
    // // Invoke
    // // Analyze
    // }
}