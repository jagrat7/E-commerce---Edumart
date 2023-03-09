package com.estore.api.estoreapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.estore.api.estoreapi.model.Audio;
import com.estore.api.estoreapi.model.Book;
import com.estore.api.estoreapi.model.Cart;
import com.estore.api.estoreapi.model.CartProducts;
import com.estore.api.estoreapi.model.CartSessions;
import com.estore.api.estoreapi.model.Product;
import com.estore.api.estoreapi.model.Product.Image;
import com.estore.api.estoreapi.model.Product.Status;
import com.estore.api.estoreapi.model.Product.Image.imageType;
import com.estore.api.estoreapi.model.UICartProducts;
import com.estore.api.estoreapi.model.UICartSessions;
import com.estore.api.estoreapi.model.UserDetails;
import com.estore.api.estoreapi.model.User_Admin;
import com.estore.api.estoreapi.model.User_Author;
import com.estore.api.estoreapi.model.User_Customer;
import com.estore.api.estoreapi.model.Video;
import com.estore.api.estoreapi.persistence.CartDAO;
import com.estore.api.estoreapi.persistence.HistoryDAO;
import com.estore.api.estoreapi.persistence.ProductDAO;
import com.estore.api.estoreapi.persistence.UserDAO;
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class StoreControllerTest {
    private StoreController storeController;
    private ProductDAO mockProductDAO;
    private CartDAO mockCartDAO;
    private UserDAO mockUserDAO;
    private HistoryDAO mockHistoryDAO;
    private Product dummyProduct;
    private Cart dummyCart;
    private UICartProducts[] dummyUICartProducts;
    private UICartSessions[] dummyUICartSessions;
    private UserDetails dummyUserDetails;

    /**
     * Before each test, create a new StoreController object and inject
     * a mock DAO's
     */
    @BeforeEach
    public void setupStoreController() {
        Random random = new Random();
        mockProductDAO = mock(ProductDAO.class);
        mockCartDAO = mock(CartDAO.class);
        mockUserDAO = mock(UserDAO.class);
        mockHistoryDAO = mock(HistoryDAO.class);
        storeController = new StoreController(mockProductDAO, mockCartDAO, mockUserDAO, mockHistoryDAO);
        dummyProduct = getDummyProduct(random.nextInt(6,10) , random.nextInt(6,10) , random.nextInt(6,10) );
        dummyCart = getDummyCart(5);
        dummyUICartProducts = getDummyUICartProducts();
        dummyUICartSessions = getDummyUICartSessions();
        dummyUserDetails = getDummyUserDetails();
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

    private CartSessions[] getDummyCartSessions(int sessionSize){
        List<CartSessions> currentCartSessions = new ArrayList<CartSessions>();
        for(int i = 1; i <= sessionSize; i++){
            currentCartSessions.add(new CartSessions("dummy" + i, (int)System.currentTimeMillis(), "live"));
        }

        CartSessions[] cartSessions = new CartSessions[currentCartSessions.size()];
        currentCartSessions.toArray(cartSessions);

        return cartSessions;
    }

    private Cart getDummyCart(int productsSize) {
        Random random = new Random();
        List<String> types = Arrays.asList("video", "book", "audio");
        Cart cart = new Cart(1, null, null);
        List<CartProducts> currentCartProducts = new ArrayList<CartProducts>();
        for (int j = 0; j < productsSize; j++) {
            currentCartProducts.add(new CartProducts(types.get(random.nextInt(0, types.size())), random.nextInt(1, 6),
                    random.nextInt(1, 10)));
        }
        CartProducts[] cartProducts = new CartProducts[currentCartProducts.size()];
        currentCartProducts.toArray(cartProducts);
        cart.setProducts(cartProducts);
        cart.setSessions(getDummyCartSessions(5));
        return cart;
    }

    public UICartProducts[] getDummyUICartProducts(){
        CartProducts[] cartProducts = dummyCart.getProducts();
        List<UICartProducts> uiCartList = new ArrayList<UICartProducts>();
        for(CartProducts currentProduct : cartProducts){
            Object productData = null;
            String type = null;
            if(currentProduct.getType().equals("video")){
                productData = findCorrespondingProduct(dummyProduct.getVideos(), currentProduct.getId());
                type = "video";
            }else if(currentProduct.getType().equals("book")){
                productData = findCorrespondingProduct(dummyProduct.getBooks(), currentProduct.getId());
                type = "book";
            }else{
                productData = findCorrespondingProduct(dummyProduct.getAudios(), currentProduct.getId());
                type = "audio";
            }
            uiCartList.add(new UICartProducts(currentProduct.getQuantity(), type, productData));
        }
        UICartProducts[] UICartProductsArray = new UICartProducts[uiCartList.size()];
        uiCartList.toArray(UICartProductsArray);
        return UICartProductsArray;
    }

    public UICartSessions[] getDummyUICartSessions(){
        CartSessions[] cartSessions = dummyCart.getSessions();
        List<UICartSessions> uiCartList = new ArrayList<UICartSessions>();
        for(CartSessions currentSession : cartSessions){
            uiCartList.add(new UICartSessions(currentSession.getTime(), currentSession.getName(), currentSession.getStatus()));
        }
        UICartSessions[] UICartSessionsArray = new UICartSessions[uiCartList.size()];
        uiCartList.toArray(UICartSessionsArray);
        return UICartSessionsArray;
    }

    private UserDetails getDummyUserDetails(){
        UserDetails userDtls = new UserDetails(1, "dummy", "dummy", "dummy", 5152459812l, "dummy", "dummy", "dummy@gmail.com");
        HashMap<String,Object> wallet_details = new HashMap<String,Object>();
        wallet_details.put("id", 22333);
        userDtls.setCustomer(new User_Customer[] {new User_Customer(userDtls.getId(), userDtls.getUsername(), userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), "wallet", null, wallet_details)});
        return userDtls;
    }

    private int getUICartProductId(UICartProducts UICartProduct){
        if(UICartProduct.getType().equals("video")){
            return ( (Video)UICartProduct.getProductDetails() ).getId();
        }else if(UICartProduct.getType().equals("book")){
            return ( (Book)UICartProduct.getProductDetails() ).getId();
        }else{
            return ( (Audio)UICartProduct.getProductDetails() ).getId();
        }
    }
    

    private Product findCorrespondingProduct(Product[] products, int id){
        for(Product product : products){
            if(product.getId() == id){
                return product;
            }
        }
        return null;
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

    public void mockUserPermission(boolean isValidUser, int user_id){
        when(mockUserDAO.authenticateUser(user_id, 0)).thenReturn(isValidUser);
        when(mockUserDAO.authenticateUser(user_id, 1)).thenReturn(isValidUser);
        when(mockUserDAO.authenticateUser(user_id, 2)).thenReturn(isValidUser);
        when(mockUserDAO.authenticateUser(user_id, 3)).thenReturn(isValidUser);
        when(mockUserDAO.authenticateUser(user_id, 4)).thenReturn(isValidUser);
    }

    public void mockParticularUserPermission(boolean isValidUser, int user_id, List<Integer> userTypes){
        if(userTypes.contains(0)){
            when(mockUserDAO.authenticateUser(user_id, 0)).thenReturn(isValidUser);
        }
        if(userTypes.contains(1)){
            when(mockUserDAO.authenticateUser(user_id, 1)).thenReturn(isValidUser);
        }
        if(userTypes.contains(2)){
            when(mockUserDAO.authenticateUser(user_id, 2)).thenReturn(isValidUser);
        }
        if(userTypes.contains(3)){
            when(mockUserDAO.authenticateUser(user_id, 3)).thenReturn(isValidUser);
        }
        if(userTypes.contains(4)){
            when(mockUserDAO.authenticateUser(user_id, 4)).thenReturn(isValidUser);
        }
    }

    public <T> List<T> getRandomSubList(List<T> list){
        Random random = new Random();
        int startIndex = random.nextInt(0, list.size());
        int endIndex = random.nextInt(startIndex, list.size());
       return list != null ? list.subList(startIndex, endIndex) : null;
    }

    @Test
    public void testGetProducts() throws IOException  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProducts()).thenReturn(product);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getAllProducts(getDummyUserDetails(3).getId(), null, null);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(product,response.getBody());
    }

    @Test
    public void testGetProducts_UnAuthorized() throws IOException  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProducts()).thenReturn(product);
        mockUserPermission(false, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getAllProducts(getDummyUserDetails(3).getId(), null, null);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testGetProducts_ProductNotFound() throws IOException  {
        when(mockProductDAO.getProducts()).thenReturn(null);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getAllProducts(getDummyUserDetails(3).getId(), null, null);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testGetProducts_InternalServerError() throws IOException  {
        // Setup
        when(mockProductDAO.getProducts()).thenThrow(new IOException()).thenReturn(null);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getAllProducts(getDummyUserDetails(3).getId(), null, null);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testGetProduct() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenReturn(product.getVideos()[0]);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(product.getVideos()[0],response.getBody());
    }

    @Test
    public void testGetProduct_UnAuthorized() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenReturn(product.getVideos()[0]);
        mockUserPermission(false, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testGetProduct_ProductNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenReturn(null);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
    
    @Test
    public void testGetProduct_TypeNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenThrow(new Exception("TYPE_NOT_FOUND"));
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testGetProduct_InternalServerError() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenThrow(new Exception());
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.getProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testCreateProduct() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        when(mockProductDAO.createProduct(map)).thenReturn(product.getVideos()[0]);
        when(mockUserDAO.getUserData(getDummyUserDetails(1).getId())).thenReturn(getDummyUserDetails(1));
        mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.createProduct(map, getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(product.getVideos()[0],response.getBody());
    }

    @Test
    public void testCreateProduct_UnAuthorized() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        when(mockProductDAO.createProduct(map)).thenReturn(product.getVideos()[0]);
        mockUserPermission(false, getDummyUserDetails(2).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.createProduct(map, getDummyUserDetails(2).getId());

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testCreateProduct_ProductNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        when(mockProductDAO.createProduct(map)).thenReturn(null);
        List<Integer> userTypes = new ArrayList<>();
        userTypes.add(1);
        //userTypes.add(2);
        mockParticularUserPermission(true, getDummyUserDetails(1).getId(),userTypes);
        //mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.createProduct(map, getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }
    
    @Test
    public void testCreateProduct_TypeNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        map.put(CodeSmellFixer.SnakeCase.FROM_SRC, CodeSmellFixer.LowerCase.ADMIN);
        map.put(CodeSmellFixer.CamelCase.CREATED_BY, getDummyUserDetails(1).getId());
        List<Integer> userTypes = new ArrayList<>();
        userTypes.add(1);
        //userTypes.add(2);
        mockParticularUserPermission(true, getDummyUserDetails(1).getId(),userTypes);

        when(mockProductDAO.createProduct(map)).thenThrow(new Exception("TYPE_NOT_FOUND"));
        //mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.createProduct(map, getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testCreateProduct_InternalServerError() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        when(mockProductDAO.createProduct(map)).thenThrow(new Exception());
        mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.createProduct(map, getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testSearchProduct() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.findProducts("Fired")).thenReturn(product);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.searchProducts("Fired", "Products", getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(product,response.getBody());
    }

    @Test
    public void testSearchProduct_WithoutType() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.findProducts("Fired")).thenReturn(product);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.searchProducts("Fired", null, getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(product,response.getBody());
    }

    @Test
    public void testSearchProduct_UnAuthorized() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.findProducts("Fired")).thenReturn(product);
        mockUserPermission(false, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.searchProducts("Fired", "Products", getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testSearchProduct_InvalidProductsParam() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.findProducts("Fired")).thenReturn(product);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.searchProducts("Fired", "ProductsInvalid", getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testSearchProduct_InternalServerError() throws Exception  {
        // Setup
        when(mockProductDAO.findProducts("Fired")).thenThrow(new IOException());
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.searchProducts("Fired", "Products", getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testDeleteProduct() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.deleteProduct(product.getVideos()[0].getId(), "video")).thenReturn(true);
        
        Product dummyProduct = new Product();
        dummyProduct.setVideos(new Video[]{ product.getVideos()[0] });
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenReturn(dummyProduct);
        
        mockUserPermission(true, getDummyUserDetails(1).getId());

        HashMap<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("id", product.getVideos()[0].getId());
        expectedResponse.put("status", "deleted");
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.deleteProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(expectedResponse,response.getBody());
    }

    @Test
    public void testDeleteProduct_UnAuthorized() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.deleteProduct(product.getVideos()[0].getId(), "video")).thenReturn(true);
        
        Product dummyProduct = new Product();
        dummyProduct.setVideos(new Video[]{ product.getVideos()[0] });
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenReturn(dummyProduct);
        
        mockUserPermission(false, getDummyUserDetails(2).getId());

        HashMap<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("id", product.getVideos()[0].getId());
        expectedResponse.put("status", "deleted");
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.deleteProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(2).getId());

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }
    
    @Test
    public void testDeleteProduct_TypeNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.deleteProduct(product.getVideos()[0].getId(), "video")).thenReturn(true);
        
        Product dummyProduct = new Product();
        dummyProduct.setVideos(new Video[]{ product.getVideos()[0] });
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenThrow(new Exception("TYPE_NOT_FOUND"));
        
        mockUserPermission(true, getDummyUserDetails(1).getId());

        HashMap<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("id", product.getVideos()[0].getId());
        expectedResponse.put("status", "deleted");
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.deleteProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testDeleteProduct_ProductNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.deleteProduct(product.getVideos()[0].getId(), "video")).thenReturn(true);
        
        Product dummyProduct = new Product();
        dummyProduct.setVideos(new Video[]{ product.getVideos()[0] });
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenReturn(null);
        
        mockUserPermission(true, getDummyUserDetails(1).getId());

        HashMap<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("id", product.getVideos()[0].getId());
        expectedResponse.put("status", "deleted");
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.deleteProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testDeleteProduct_InternalServerError() throws Exception  {
        // Setup
        Product product = dummyProduct;
        when(mockProductDAO.deleteProduct(product.getVideos()[0].getId(), "video")).thenReturn(true);
        
        Product dummyProduct = new Product();
        dummyProduct.setVideos(new Video[]{ product.getVideos()[0] });
        when(mockProductDAO.getProduct(product.getVideos()[0].getId(), "video")).thenThrow(new Exception());
        
        mockUserPermission(true, getDummyUserDetails(1).getId());

        HashMap<String, Object> expectedResponse = new HashMap<String, Object>();
        expectedResponse.put("id", product.getVideos()[0].getId());
        expectedResponse.put("status", "deleted");
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.deleteProduct("video", product.getVideos()[0].getId(), getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testUpdateProduct() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put("name", map.get("name") + "_updated");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);
        

        Video expectedVideo = mapper.convertValue(map, Video.class);
        Product expectedProduct = new Product();
        expectedProduct.setVideos(new Video[]{expectedVideo});

        when(mockProductDAO.updateProduct(map)).thenReturn(expectedProduct);
       
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        
        mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.updateProduct(map, getDummyUserDetails(1).getId(), null);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(expectedProduct,response.getBody());
    }

    @Test
    public void testUpdateProduct_UnAuthorized() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put("name", map.get("name") + "_updated");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);

        Video expectedVideo = mapper.convertValue(map, Video.class);
        Product expectedProduct = new Product();
        expectedProduct.setVideos(new Video[]{expectedVideo});

        when(mockProductDAO.updateProduct(map)).thenReturn(expectedProduct);
       
        
        mockUserPermission(false, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.updateProduct(map, getDummyUserDetails(3).getId(), null);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testUpdateProduct_ProductNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put("name", map.get("name") + "_updated");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);

        Video expectedVideo = mapper.convertValue(map, Video.class);
        Product expectedProduct = new Product();
        expectedProduct.setVideos(new Video[]{expectedVideo});

        when(mockProductDAO.updateProduct(map)).thenReturn(null);
       
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);
        
        mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.updateProduct(map, getDummyUserDetails(1).getId(), null);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
    
    @Test
    public void testUpdateProduct_TypeNotFound() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put("name", map.get("name") + "_updated");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);

        Video expectedVideo = mapper.convertValue(map, Video.class);
        Product expectedProduct = new Product();
        expectedProduct.setVideos(new Video[]{expectedVideo});

        when(mockProductDAO.updateProduct(map)).thenThrow(new Exception("TYPE_NOT_FOUND"));
       
        
        mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.updateProduct(map, getDummyUserDetails(1).getId(), null);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testUpdateProduct_InternalServerError() throws Exception  {
        // Setup
        Product product = dummyProduct;
        ObjectMapper mapper = new ObjectMapper();

        // Convert POJO to Map
        HashMap<String, Object> map = mapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        map.put("name", map.get("name") + "_updated");
        map.put("price", Float.valueOf(map.get("price").toString()) + 500);

        Video expectedVideo = mapper.convertValue(map, Video.class);
        Product expectedProduct = new Product();
        expectedProduct.setVideos(new Video[]{expectedVideo});

        when(mockProductDAO.updateProduct(map)).thenThrow(new Exception());
       
        map.put(CodeSmellFixer.LowerCase.TYPE, CodeSmellFixer.LowerCase.VIDEO);

        mockUserPermission(true, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<Product> response = storeController.updateProduct(map, getDummyUserDetails(1).getId(), null);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testGetAllCartProducts() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        UICartSessions[] uiCartSessions = dummyUICartSessions;
       
        HashMap<String, Object> cartData = new HashMap<>();
        cartData.put(CodeSmellFixer.LowerCase.PRODUCTS, uiCartProducts);
        cartData.put(CodeSmellFixer.LowerCase.SESSIONS, uiCartSessions);

        when(mockCartDAO.getCartProductsAndSessions(getDummyUserDetails(3).getId())).thenReturn(cartData);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.getAllCartProducts(getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(cartData,response.getBody());
    }

    @Test
    public void testGetAllCartProducts_UnAuthorized() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        UICartSessions[] uiCartSessions = dummyUICartSessions;
       
        HashMap<String, Object> cartData = new HashMap<>();
        cartData.put(CodeSmellFixer.LowerCase.PRODUCTS, uiCartProducts);
        cartData.put(CodeSmellFixer.LowerCase.SESSIONS, uiCartSessions);

        when(mockCartDAO.getCartProductsAndSessions(getDummyUserDetails(1).getId())).thenReturn(cartData);
        mockUserPermission(false, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.getAllCartProducts(getDummyUserDetails(1).getId());

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testGetAllCartProducts_CartNotFound() throws Exception{
        // Setup
        when(mockCartDAO.getCartProductsAndSessions(getDummyUserDetails(3).getId())).thenReturn(null);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.getAllCartProducts(getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testGetAllCartProducts_TypeNotFound() throws Exception{
        // Setup
        when(mockCartDAO.getCartProductsAndSessions(getDummyUserDetails(3).getId())).thenThrow(new Exception("TYPE_NOT_FOUND"));
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.getAllCartProducts(getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testGetAllCartProducts_InternalServerError() throws Exception  {
        // Setup
        when(mockCartDAO.getCartProductsAndSessions(getDummyUserDetails(3).getId())).thenThrow(new Exception());
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<HashMap<String, Object>> response = storeController.getAllCartProducts(getDummyUserDetails(3).getId());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testUpdateCartProducts_IncreaseProductQuantity() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(uiCartProducts[0]));
        product.put("type", uiCartProducts[0].getType());
        product.put("quantity", 10);
        product.put("isIncrease", true);

        when(mockCartDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenReturn(uiCartProducts[0]);
        mockUserPermission(true, getDummyUserDetails(3).getId());

        uiCartProducts[0].setQuantity(uiCartProducts[0].getQuantity() + 10);
        // Invoke
        ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(3).getId(), product);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(uiCartProducts[0],response.getBody());
    }

    @Test
    public void testUpdateCartProducts_DecreaseProductQuantity() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(uiCartProducts[0]));
        product.put("type", uiCartProducts[0].getType());
        product.put("quantity", 10);
        product.put("isIncrease", false);

        when(mockCartDAO.decreaseProductInCartForUser(getDummyUserDetails(3).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenReturn(uiCartProducts[0]);
        mockUserPermission(true, getDummyUserDetails(3).getId());

        uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
        // Invoke
        ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(3).getId(), product);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(uiCartProducts[0],response.getBody());
    }

    @Test
    public void testUpdateCartProducts_UnAuthorized() throws Exception{
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(uiCartProducts[0]));
       product.put("type", uiCartProducts[0].getType());
       product.put("quantity", 10);
       product.put("isIncrease", true);

       when(mockCartDAO.increaseProductInCartForUser(getDummyUserDetails(1).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenReturn(uiCartProducts[0]);
       mockUserPermission(false, getDummyUserDetails(1).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(1).getId(), product);

       // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testUpdateCartProducts_CartNotFound() throws Exception{
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(uiCartProducts[0]));
       product.put("type", uiCartProducts[0].getType());
       product.put("quantity", 10);
       product.put("isIncrease", true);

       when(mockCartDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenReturn(null);
       mockUserPermission(true, getDummyUserDetails(3).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(3).getId(), product);

       // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testUpdateCartProducts_TypeNotFound() throws Exception{
         // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(uiCartProducts[0]));
       product.put("type", uiCartProducts[0].getType());
       product.put("quantity", 10);
       product.put("isIncrease", true);

       when(mockCartDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenThrow(new Exception("TYPE_NOT_FOUND"));
       mockUserPermission(true, getDummyUserDetails(3).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(3).getId(), product);

       // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testUpdateCartProducts_InvalidBody() throws Exception{
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(uiCartProducts[0]));
       product.put("type", uiCartProducts[0].getType());
       product.put("quantity", 10);

       when(mockCartDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenReturn(uiCartProducts[0]);
       mockUserPermission(true, getDummyUserDetails(3).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(3).getId(), product);

       // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testUpdateCartProducts_InternalServerError() throws Exception  {
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(uiCartProducts[0]));
       product.put("type", uiCartProducts[0].getType());
       product.put("quantity", 10);
       product.put("isIncrease", true);

       when(mockCartDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(),
                                                    Integer.parseInt(product.get("id").toString()),
                                                    product.get("type").toString(),
                                                    Integer.parseInt(product.get("quantity").toString()))).thenThrow(new Exception());
       mockUserPermission(true, getDummyUserDetails(3).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts> response = storeController.updateCartProducts(getDummyUserDetails(3).getId(), product);

       // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testAddProductsInCart() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];

        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        product.put("quantity", tempCartProducts.getQuantity());

        when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenReturn(uiCartProducts);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(3).getId(), product);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(uiCartProducts,response.getBody());
    }

    @Test
    public void testAddProductsInCart_WithoutQuantity() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];

        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        
        uiCartProducts[0].setQuantity(1);
        
        when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenReturn(uiCartProducts);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(3).getId(), product);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(uiCartProducts,response.getBody());
    }

    @Test
    public void testAddProductsInCart_UnAuthorized() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        product.put("quantity", tempCartProducts.getQuantity());
        
        when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenReturn(uiCartProducts);
        mockUserPermission(false, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testAddProductsInCart_CartNotFound() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        product.put("quantity", tempCartProducts.getQuantity());
        
        when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenReturn(null);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testAddProductsInCart_TypeNotFound() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        product.put("quantity", tempCartProducts.getQuantity());
        
        when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenThrow(new Exception("TYPE_NOT_FOUND"));
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testAddProductsInCart_InvalidBody() throws Exception{
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;

       UICartProducts tempCartProducts =  uiCartProducts[0];
        
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(tempCartProducts));
       product.put("quantity", tempCartProducts.getQuantity());
        
       when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenReturn(uiCartProducts);
       mockUserPermission(true, getDummyUserDetails(3).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(1).getId(), product);

       // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testAddProductsInCart_ProductsAlreadyInCart() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        product.put("quantity", tempCartProducts.getQuantity());
        
        when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenThrow(new Exception("PRODUCT_ALREADY_IN_CART"));
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED,response.getStatusCode());
    }

    @Test
    public void testAddProductsInCart_InternalServerError() throws Exception  {
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;

       UICartProducts tempCartProducts =  uiCartProducts[0];
       
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(tempCartProducts));
       product.put("type", tempCartProducts.getType());
       product.put("quantity", tempCartProducts.getQuantity());
       
       when(mockCartDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType(), tempCartProducts.getQuantity())).thenThrow(new Exception());
       mockUserPermission(true, getDummyUserDetails(3).getId());
       // Invoke
       ResponseEntity<UICartProducts[]> response = storeController.addProductsInCart(getDummyUserDetails(1).getId(), product);

       // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testDeleteProductsInCart() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];

        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());

        when(mockCartDAO.removeProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType())).thenReturn(uiCartProducts);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.deleteProductsInCart(getDummyUserDetails(3).getId(), product);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(uiCartProducts,response.getBody());
    }

    @Test
    public void testDeleteProductsInCart_UnAuthorized() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        
        when(mockCartDAO.removeProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType())).thenReturn(uiCartProducts);
        mockUserPermission(false, getDummyUserDetails(1).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.deleteProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void testDeleteProductsInCart_CartNotFound() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        
        when(mockCartDAO.removeProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType())).thenReturn(null);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.deleteProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testDeleteProductsInCart_TypeNotFound() throws Exception{
        // Setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;

        UICartProducts tempCartProducts =  uiCartProducts[0];
        
        HashMap<String,Object> product = new HashMap<String,Object>();
        product.put("id", getUICartProductId(tempCartProducts));
        product.put("type", tempCartProducts.getType());
        
        when(mockCartDAO.removeProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType())).thenThrow(new Exception("TYPE_NOT_FOUND"));
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<UICartProducts[]> response = storeController.deleteProductsInCart(getDummyUserDetails(1).getId(), product);

        // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testDeleteProductsInCart_InvalidBody() throws Exception{
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;

       UICartProducts tempCartProducts =  uiCartProducts[0];
        
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(tempCartProducts));
        
       when(mockCartDAO.removeProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType())).thenReturn(uiCartProducts);
       mockUserPermission(true, getDummyUserDetails(3).getId());

       uiCartProducts[0].setQuantity( ( uiCartProducts[0].getQuantity() - 10 ) > 0 ?  uiCartProducts[0].getQuantity() - 10 : 0);
       // Invoke
       ResponseEntity<UICartProducts[]> response = storeController.deleteProductsInCart(getDummyUserDetails(1).getId(), product);

       // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void testDeleteProductsInCart_InternalServerError() throws Exception  {
       // Setup
       UICartProducts[] uiCartProducts = dummyUICartProducts;

       UICartProducts tempCartProducts =  uiCartProducts[0];
       
       HashMap<String,Object> product = new HashMap<String,Object>();
       product.put("id", getUICartProductId(tempCartProducts));
       product.put("type", tempCartProducts.getType());
       
       when(mockCartDAO.removeProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(tempCartProducts), tempCartProducts.getType())).thenThrow(new Exception());
       mockUserPermission(true, getDummyUserDetails(3).getId());
       // Invoke
       ResponseEntity<UICartProducts[]> response = storeController.deleteProductsInCart(getDummyUserDetails(1).getId(), product);

       // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testAuthenticateUser() throws Exception{
        // Setup
        String userName = "dummy";
        String password = "dummy";

        HashMap<String,Object> expectedResponse = new HashMap<String,Object>();
        expectedResponse.put("is_admin", false);
        expectedResponse.put("is_customer", true);
        expectedResponse.put("is_author", false);
        expectedResponse.put("user_data", dummyUserDetails);

        HashMap<String,String> credentials = new HashMap<String,String>();
        credentials.put("username", userName);
        credentials.put("password", password);

        when(mockUserDAO.getUserData(userName, password)).thenReturn(dummyUserDetails);
        when(mockUserDAO.authenticateAdmin(userName, password)).thenReturn(false);
        when(mockUserDAO.authenticateCustomer(userName, password)).thenReturn(true);
        when(mockUserDAO.authenticateAuthor(userName, password)).thenReturn(false);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<HashMap<String,Object>> response = storeController.authenticateUser(credentials);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(expectedResponse,response.getBody());
    }

    @Test
    public void testAuthenticateUser_NotFound() throws Exception{
        // Setup
        String userName = "dummy";
        String password = "dummy";

        HashMap<String,Object> expectedResponse = new HashMap<String,Object>();
        expectedResponse.put("is_admin", false);
        expectedResponse.put("is_customer", true);
        expectedResponse.put("is_author", false);
        expectedResponse.put("user_data", dummyUserDetails);

        HashMap<String,String> credentials = new HashMap<String,String>();
        credentials.put("username", userName);
        credentials.put("password", "password");

        when(mockUserDAO.getUserData(userName, password)).thenReturn(dummyUserDetails);
        when(mockUserDAO.authenticateAdmin(userName, password)).thenReturn(false);
        when(mockUserDAO.authenticateCustomer(userName, password)).thenReturn(true);
        when(mockUserDAO.authenticateAuthor(userName, password)).thenReturn(false);
        mockUserPermission(true, getDummyUserDetails(3).getId());
        // Invoke
        ResponseEntity<HashMap<String,Object>> response = storeController.authenticateUser(credentials);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testAuthenticateUser_InvalidBody() throws Exception{
       // Setup
       String userName = "dummy";
       String password = "dummy";

       HashMap<String,Object> expectedResponse = new HashMap<String,Object>();
       expectedResponse.put("is_admin", false);
       expectedResponse.put("is_customer", true);
       expectedResponse.put("is_author", false);
       expectedResponse.put("user_data", dummyUserDetails);

       HashMap<String,String> credentials = new HashMap<String,String>();
       credentials.put("username", userName);

       when(mockUserDAO.getUserData(userName, password)).thenReturn(dummyUserDetails);
       when(mockUserDAO.authenticateAdmin(userName, password)).thenReturn(false);
        when(mockUserDAO.authenticateCustomer(userName, password)).thenReturn(true);
        when(mockUserDAO.authenticateAuthor(userName, password)).thenReturn(false);
        // Invoke
        ResponseEntity<HashMap<String,Object>> response = storeController.authenticateUser(credentials);

       // Analyze
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }
}