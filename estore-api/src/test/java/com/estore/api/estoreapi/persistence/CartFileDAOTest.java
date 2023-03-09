package com.estore.api.estoreapi.persistence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
import com.estore.api.estoreapi.model.Cart;
import com.estore.api.estoreapi.model.CartProducts;
import com.estore.api.estoreapi.model.CartSessions;
import com.estore.api.estoreapi.model.History;
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
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CartFileDAOTest {
    private UserDAO mockUserDAO;
    private HistoryDAO mockHistoryDAO;
    private Product dummyProduct;
    private Cart dummyCart;
    private UICartProducts[] dummyUICartProducts;
    private UICartSessions[] dummyUICartSessions;
    private CartFileDAO cartFileDAO;
    private ProductFileDAO productFileDAO;
    private UserFileDAO userFileDAO;
    private UserDetails dummyUserDetails;


    /**
     * Before each test, create a new StoreController object and inject
     * a mock DAO's
     * @throws IOException
     * @throws DatabindException
     * @throws StreamReadException
     */
    @BeforeEach
    public void setupStoreController() throws Exception {
        Random random = new Random();
        List<String> allowedtypesmock = new ArrayList<String>();
        allowedtypesmock.add("video");
        allowedtypesmock.add("audio");
        allowedtypesmock.add("book");
        mockUserDAO = mock(UserDAO.class);
        dummyProduct = getDummyProduct(random.nextInt(6,10) , random.nextInt(6,10) , random.nextInt(6,10) );
        dummyCart = getDummyCart(5,dummyProduct);
        dummyUICartProducts = getDummyUICartProducts();
        dummyUICartSessions = getDummyUICartSessions();
        dummyUserDetails = getDummyUserDetails();
        ObjectMapper objectMapper4 = Mockito.mock(ObjectMapper.class);
        ObjectMapper objectMapper3 = Mockito.mock(ObjectMapper.class);
        ObjectMapper objectMapper2 = Mockito.mock(ObjectMapper.class);
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        when(objectMapper.readValue(new File("test.json"), Cart[].class)).thenReturn(new Cart[] {dummyCart});
        when(objectMapper2.readValue(new File("test1.json"), Product.class)).thenReturn(dummyProduct);
        when(objectMapper3.readValue(new File("test2.json"), History.class)).thenReturn(null);
        when(objectMapper4.readValue(new File("test3.json"), UserDetails.class)).thenReturn(dummyUserDetails);
        productFileDAO = new ProductFileDAO("test1.json", allowedtypesmock, objectMapper2);
        mockHistoryDAO = mock(HistoryDAO.class);
        mockUserDAO = mock(UserDAO.class);
        userFileDAO=new UserFileDAO("test3.json",objectMapper4 );
        cartFileDAO=new CartFileDAO("test.json",objectMapper,productFileDAO, mockHistoryDAO, userFileDAO );

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

    private Cart getDummyCart(int productsSize, Product dummyProduct) {
        Random random = new Random();
        Video[] videos = dummyProduct.getVideos();
        Audio[] audios = dummyProduct.getAudios();
        Book[] books = dummyProduct.getBooks();
        List<String> types = Arrays.asList("video", "book", "audio");
        Cart cart = new Cart(1, null, null);
        List<CartProducts> currentCartProducts = new ArrayList<CartProducts>();
        for (int j = 0; j < productsSize; j++) {
                    String type = types.get(random.nextInt(0, types.size()));
                    if(type.equalsIgnoreCase("video")){
                        currentCartProducts.add(new CartProducts(type, videos[random.nextInt(0, videos.length - 1)].getId(),
                        random.nextInt(2, 10)));
                    }else if(type.equalsIgnoreCase("book")){
                        currentCartProducts.add(new CartProducts(type, books[random.nextInt(0, books.length - 1)].getId(),
                        random.nextInt(2, 10)));
                    }else{
                        currentCartProducts.add(new CartProducts(type, audios[random.nextInt(0, audios.length - 1)].getId(),
                        random.nextInt(2, 10)));
                    }
        }
        CartProducts[] cartProducts = new CartProducts[currentCartProducts.size()];
        currentCartProducts.toArray(cartProducts);
        cart.setProducts(cartProducts);
        cart.setSessions(getDummyCartSessions(5));
        return cart;
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
    public UICartProducts[] getDummyUICartProduct(){
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
        userDtls.setAdmin(new User_Admin[]{new User_Admin(userDtls.getId(), userDtls.getUsername(), userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail())});
        userDtls.setAuthor(new User_Author[0]);
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

    public <T> List<T> getRandomSubList(List<T> list){
        Random random = new Random();
        int startIndex = random.nextInt(0, list.size());
        int endIndex = random.nextInt(startIndex, list.size());
       return list != null ? list.subList(startIndex, endIndex) : null;
    }

    public void assertEqualsUICarts(UICartProducts[] expected, UICartProducts[] actual){
        assertEquals(expected.length, actual.length);
        for(int i = 0; i < expected.length; i++){
            assertEquals(expected[i].getProductDetails(), actual[i].getProductDetails());
            assertEquals(expected[i].getType(), actual[i].getType());
            assertEquals(expected[i].getQuantity(), actual[i].getQuantity());
        }
    }
    public void assertEqualsUICart(UICartProducts expected, UICartProducts actual){
            assertEquals(expected.getProductDetails(), actual.getProductDetails());
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getQuantity(), actual.getQuantity());
    }

    public void assertEqualsUISessions(UICartSessions[] expected, UICartSessions[] actual){
        assertEquals(expected.length, actual.length);
        for(int i = 0; i < expected.length; i++){
            assertEquals(expected[i].getName(), actual[i].getName());
            assertEquals(expected[i].getStatus(), actual[i].getStatus());
            assertEquals(expected[i].getTime(), actual[i].getTime());
        }
    }
    public void assertEqualsUISessions(UICartSessions expected, UICartSessions actual){
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getStatus(), actual.getStatus());
            assertEquals(expected.getTime(), actual.getTime());
    }

    @Test
    public void testgetProductsInCartForUser() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        UICartSessions[] uiCartSessions = dummyUICartSessions;
       
        //Invoke
        HashMap<String, Object> response=cartFileDAO.getCartProductsAndSessions(getDummyUserDetails(3).getId());
        //analyze
        assertEqualsUICarts(uiCartProducts, (UICartProducts[])response.get(CodeSmellFixer.LowerCase.PRODUCTS));
        assertEqualsUISessions(uiCartSessions, (UICartSessions[])response.get(CodeSmellFixer.LowerCase.SESSIONS));
    }

    @Test
    public void testgetProductsIncartForUser_UserHasNoProduct() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = null;
        //Invoke
        UICartProducts[] response=cartFileDAO.getProductsInCartForUser(0);
        //analyze
        assertEquals(uiCartProducts, response);
    }

    @Test
    public void testCheckProductsAvailableInCartForUser() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        //Invoke
        Boolean response=cartFileDAO.checkProductsAvailableInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]),uiCartProducts[0].getType() );
        //analyze
        assertEquals(true, response);
    }

    @Test
    public void testCheckProductsAvailableInCartForUser_TypeNotFound() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        //Invoke
        try{
            cartFileDAO.checkProductsAvailableInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]),"sun" );
        }catch(Exception e){
        //analyze
        assertEquals("TYPE_NOT_FOUND", e.getMessage());
        }
    }

    @Test
    public void testCheckProductsAvailableInCartForUser_NoUserID() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        //Invoke
        Boolean response=cartFileDAO.checkProductsAvailableInCartForUser(0, getUICartProductId(uiCartProducts[0]),uiCartProducts[0].getType() );
        //Analyze
        assertEquals(false, response);
    }

    @Test
    public void testIncreaseProductInCartForUser() throws Exception {
        //setup
        UICartProducts[] uiCartProducts=dummyUICartProducts;
        //Invoke
        UICartProducts response=cartFileDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]), uiCartProducts[0].getType(), 1);
        //Analyze
        assertEquals(uiCartProducts[0].getQuantity()+1, response.getQuantity());
    }

    @Test
    public void testIncreaseProductInCartForUser_ProductNotFound() throws Exception {
        //setup
        UICartProducts[] uiCartProducts=dummyUICartProducts;
        //Invoke
        UICartProducts response=cartFileDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]), uiCartProducts[0].getType(), 1);
        //Analyze
        uiCartProducts[0].setQuantity(uiCartProducts[0].getQuantity() + 1);
        assertEqualsUICart(uiCartProducts[0], response);
    }

    @Test
    public void testIncreaseProductInCartForUser_TypeNotFound() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        //Invoke
        try{
            cartFileDAO.increaseProductInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]),"sun",1);
        }catch(Exception e){
        //Analyze
        assertEquals("TYPE_NOT_FOUND", e.getMessage());
        }
    }

    @Test
    public void testDecreaseProductInCartForUser() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        //Invoke
        UICartProducts response=cartFileDAO.decreaseProductInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]), uiCartProducts[0].getType(), 1);
        //analyze
        assertEquals(uiCartProducts[0].getQuantity()-1,response.getQuantity());
    }

    @Test
    public void testDecreaseProductInCartForUser_TypeNotFound() throws Exception{
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
        //Invoke
        try{
            cartFileDAO.decreaseProductInCartForUser(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]),"sun",1);
        }catch(Exception e){
        //Analyze
        assertEquals("TYPE_NOT_FOUND", e.getMessage());
        }
    }

    @Test
    public void testAddProductsInCart(){
        //setup
        UICartProducts[] uiCartProducts = dummyUICartProducts;
         //Invoke
         try{
            cartFileDAO.addProductsInCart(getDummyUserDetails(3).getId(), getUICartProductId(uiCartProducts[0]), uiCartProducts[0].getType(), 1);
         }catch(Exception e){
            assertEquals("PRODUCT_ALREADY_IN_CART", e.getMessage());
         }
    }
}

    
