package com.estore.api.estoreapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.estore.api.estoreapi.model.Cart;
import com.estore.api.estoreapi.model.CartProducts;
import com.estore.api.estoreapi.model.History;
import com.estore.api.estoreapi.model.InventoryHistory;
import com.estore.api.estoreapi.model.InventoryHistoryActions;
import com.estore.api.estoreapi.model.Product;
import com.estore.api.estoreapi.model.Product.Image;
import com.estore.api.estoreapi.model.Product.Status;
import com.estore.api.estoreapi.model.Product.Image.imageType;
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.estore.api.estoreapi.model.PurchaseHistory;
import com.estore.api.estoreapi.model.PurchaseHistoryProducts;
import com.estore.api.estoreapi.model.UICartProducts;
import com.estore.api.estoreapi.model.UICartSessions;
import com.estore.api.estoreapi.model.UIInventoryHistory;
import com.estore.api.estoreapi.model.UIPurchaseHistory;
import com.estore.api.estoreapi.model.UserDetails;
import com.estore.api.estoreapi.model.User_Admin;
import com.estore.api.estoreapi.model.User_Author;
import com.estore.api.estoreapi.model.User_Customer;
import com.estore.api.estoreapi.model.Video;
import com.estore.api.estoreapi.model.InventoryHistoryActions.FieldChanges;
import com.estore.api.estoreapi.model.InventoryHistoryActions.ProductType;
import com.estore.api.estoreapi.model.InventoryHistoryActions.SubType;
import com.estore.api.estoreapi.model.InventoryHistoryActions.Type;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HistoryFileDAOTest {
    private HistoryFileDAO historyFileDAO;
    private History historyDetails;
    private UICartProducts[] dummyUICartProducts;
    private Product dummyProduct;
    private Cart dummyCart;

    /**
     * Before each test, create a new StoreController object and inject
     * a mock DAO's
     * @throws IOException
     * @throws DatabindException
     * @throws StreamReadException
     */
    @BeforeEach
    public void setupHistoryFileDAOTest() throws Exception {
        Random random = new Random();
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        when(objectMapper.readValue(new File("test.json"), History.class)).thenReturn(getDummyHistoryDetails());
        historyFileDAO = new HistoryFileDAO("test.json", objectMapper);
        historyDetails = getDummyHistoryDetails();
        dummyProduct = getDummyProduct(random.nextInt(6,10) , random.nextInt(6,10) , random.nextInt(6,10) );
        dummyCart = getDummyCart(5,dummyProduct);
        dummyUICartProducts = getDummyUICartProducts();
    }

    public History getDummyHistoryDetails(){
        History history = new History(getDummyUserDetails(1).getId(), System.currentTimeMillis());
        Product product = getDummyProduct(4,5,6);
        
        PurchaseHistory purchaseHistory = new PurchaseHistory(getDummyUserDetails(1).getId(), System.currentTimeMillis());
        
        PurchaseHistoryProducts purchaseHistoryProducts = new PurchaseHistoryProducts(CodeSmellFixer.LowerCase.VIDEO, product.getVideos()[0].getId(), product.getVideos()[0].getName(), 5);
        UICartSessions sessions = new UICartSessions(12345, "dummy", "live");
        
        purchaseHistory.setProducts(new PurchaseHistoryProducts[]{purchaseHistoryProducts});
        purchaseHistory.setSessions(new UICartSessions[]{sessions});

        history.setPurchase(new PurchaseHistory[]{purchaseHistory});

        InventoryHistoryActions inventoryActions = new InventoryHistoryActions(Type.DELETED, SubType.PRODUCTS, ProductType.VIDEO, product.getVideos()[0].getName(), product.getVideos()[0].getId(), new FieldChanges[0]);
        InventoryHistory inventoryHistory = new InventoryHistory(getDummyUserDetails(1).getId(), System.currentTimeMillis());
        inventoryHistory.setActions(new InventoryHistoryActions[]{inventoryActions});

        history.setInventory(new InventoryHistory[]{inventoryHistory});

        return history;
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
        return cart;
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

    private Product findCorrespondingProduct(Product[] products, int id){
        for(Product product : products){
            if(product.getId() == id){
                return product;
            }
        }
        return null;
    }

    public UIInventoryHistory[] addAndGetInventoryActions(Integer id, String name, String type, Type crudType, Boolean isNewDataFirst){
        InventoryHistory[] history = historyDetails.getInventory();
        UIInventoryHistory[] resultHistory = new UIInventoryHistory[history.length + 1];
        InventoryHistoryActions inventoryActions = new InventoryHistoryActions(crudType, InventoryHistoryActions.SubType.PRODUCTS, InventoryHistoryActions.ProductType.forValue(type), name, id, new InventoryHistoryActions.FieldChanges[0]);
        UIInventoryHistory result = new UIInventoryHistory(new InventoryHistoryActions[]{inventoryActions}, System.currentTimeMillis());
        if(isNewDataFirst){
            resultHistory[0] = result;
            for(int i = 0; i < history.length; i++){
                resultHistory[i+1] = new UIInventoryHistory(history[i].getActions(), System.currentTimeMillis());
            }
        }else{
            
            for(int i = 0; i < history.length; i++){
                resultHistory[i] = new UIInventoryHistory(history[i].getActions(), System.currentTimeMillis());
            }
            resultHistory[history.length] = result;
        }

        return resultHistory;
    }

    public <T> List<T> getRandomSubList(List<T> list){
        Random random = new Random();
        int startIndex = random.nextInt(0, list.size());
        int endIndex = random.nextInt(startIndex, list.size());
       return list != null ? list.subList(startIndex, endIndex) : null;
    }

    public void assertEqualsPurchaseHistory(PurchaseHistoryProducts[] expected,PurchaseHistoryProducts[] actual){
        assertEquals(expected.length, actual.length);
        for(int i=0;i<expected.length;i++){
            assertEquals(expected[i].getId(), actual[i].getId());
            assertEquals(expected[i].getName(), actual[i].getName());
            assertEquals(expected[i].getQuantity(), actual[i].getQuantity());
            assertEquals(expected[i].getType(), actual[i].getType());
        }
    }

    public void assertEqualsInventoryHistory(InventoryHistoryActions[] expected,InventoryHistoryActions[] actual){
        assertEquals(expected.length, actual.length);
        for(int i = 0; i<expected.length; i++){
            FieldChanges[] expectedFieldChanges = expected[i].getFieldChanges();
            FieldChanges[] actualFieldChanges = actual[i].getFieldChanges();
            assertEquals(expectedFieldChanges.length, actualFieldChanges.length);
            for(int j = 0; j < expectedFieldChanges.length; j++){
                assertEquals(expectedFieldChanges[j].field, actualFieldChanges[j].field);
                assertEquals(expectedFieldChanges[i].old_value, actualFieldChanges[i].old_value);
                assertEquals(expectedFieldChanges[i].new_value, actualFieldChanges[i].new_value);
            }
            assertEquals(expected[i].getId(), actual[i].getId());
            assertEquals(expected[i].getName(), actual[i].getName());
            assertEquals(expected[i].getProduct_type(), actual[i].getProduct_type());
            assertEquals(expected[i].getSub_type(), actual[i].getSub_type());
            assertEquals(expected[i].getType(), actual[i].getType());
        }
    }

    public void assertEqualsUIInventoryHistory(UIInventoryHistory[] expectedUI,UIInventoryHistory[] actualUI){
        assertEquals(expectedUI.length, actualUI.length);
        for(int k = 0; k < expectedUI.length; k++){
            InventoryHistoryActions[] expected = expectedUI[k].getActions();
            InventoryHistoryActions[] actual = actualUI[k].getActions();
            assertEqualsInventoryHistory(expected, actual);
        }
    }

    @Test
    public void testGetPurchaseHistory(){
        int userId=getDummyUserDetails(1).getId();
        PurchaseHistory[] history= historyDetails.getPurchase();
        PurchaseHistoryProducts[] historyProduct=history[0].getProducts();

        UIPurchaseHistory[] expected=historyFileDAO.getPurchaseHistory(userId);
        PurchaseHistoryProducts[] exp=expected[0].getProducts();

        assertEqualsPurchaseHistory(exp, historyProduct);
    }

    @Test
    public void testGetInventoryHistory(){
        int userId = getDummyUserDetails(1).getId();
        InventoryHistory[] history = historyDetails.getInventory();
        InventoryHistoryActions[] expectedActions = history[0].getActions();

        UIInventoryHistory[] actual = historyFileDAO.getInventoryHistory(userId);
        InventoryHistoryActions[] actualActions = actual[0].getActions();

        assertEqualsInventoryHistory(actualActions, expectedActions);
    }

    @Test
    public void testAddPurchaseHistory() throws Exception{
        historyFileDAO.addPurchaseHistory(getDummyUserDetails(1).getId(), historyDetails.getTimeStamp(), historyDetails.getPurchase()[0].getProducts(), null);
        assertTrue(true);
    }

    @Test
    public void testAddInvetoryHistory() throws Exception{
        historyFileDAO.addInventoryHistory(getDummyUserDetails(1).getId(), historyDetails.getTimeStamp(), historyDetails.getInventory()[0].getActions());
        assertTrue(true);
    }

    @Test
    public void testAddInvetoryHistoryForProducts() throws Exception{
        int userId = getDummyUserDetails(1).getId();
        InventoryHistory[] history = historyDetails.getInventory();
        InventoryHistoryActions[] expectedActions = history[0].getActions();

        HashMap<String,Object> data=new HashMap<>();
        data.put(CodeSmellFixer.LowerCase.ID, 100);
        data.put(CodeSmellFixer.LowerCase.NAME,"newName");
        data.put(CodeSmellFixer.LowerCase.TYPE,CodeSmellFixer.LowerCase.VIDEO);
        historyFileDAO.addInventoryHistoryForProducts(userId, historyDetails.getTimeStamp(), null, data, Type.DELETED);
        
        UIInventoryHistory[] actual = historyFileDAO.getInventoryHistory(userId);
        UIInventoryHistory[] expected = addAndGetInventoryActions(100, "newName", CodeSmellFixer.LowerCase.VIDEO, Type.DELETED, actual[0].getActions()[0].getId() == 100);
        assertEqualsUIInventoryHistory(expected, actual);
    }

    @Test
    public void testAddPurchaseHistoryForCustomer() throws Exception{
        historyFileDAO.addPurchaseHistoryForCustomer(getDummyUserDetails(1).getId(),historyDetails.getTimeStamp(),dummyUICartProducts , historyDetails.getPurchase()[0].getSessions());
        assertTrue(true);
    }

}
