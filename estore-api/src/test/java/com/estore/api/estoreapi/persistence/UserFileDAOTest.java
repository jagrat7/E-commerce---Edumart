package com.estore.api.estoreapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.estore.api.estoreapi.model.UserDetails;
import com.estore.api.estoreapi.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserFileDAOTest {
    private UserDetails dummyUserDetails;
    private UserFileDAO userFileDAO;

    /**
     * Before each test, create a new UserFileDAOTest object and inject
     * a mock DAO's
     */
    @BeforeEach
    public void setupUserFileDAO() throws Exception {
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        dummyUserDetails = getDummyUserDetails();
        forCodeCoverage();

        when(objectMapper.readValue(new File("userDetailsTest.json"), UserDetails.class)).thenReturn(dummyUserDetails);
        userFileDAO = new UserFileDAO("userDetailsTest.json", objectMapper);
    }

    private UserDetails getDummyUserDetails(){
        UserDetails userDtls = new UserDetails(1, "dummy", "dummy", "dummy", 5152459812l, "dummy", "dummy", "dummy@gmail.com");

        HashMap<String,Object> wallet_details = new HashMap<String,Object>();
        wallet_details.put("id", 22333);

        HashMap<String,Object> card_details = new HashMap<String,Object>();
        card_details.put("number", 34324234);
        card_details.put("expiryDate", "11/23");
        card_details.put("type", "debit");

        User_Customer[] userCustomerArray = new User_Customer[3];
        userCustomerArray[0] = new User_Customer(userDtls.getId() * 200, userDtls.getUsername() + "_user_" + 1, userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), "wallet", null, wallet_details);
        userCustomerArray[1] = new User_Customer((userDtls.getId() + 1)  * 200, userDtls.getUsername() + "_user_"  + 2, userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), "card", card_details, null);
        userCustomerArray[2] = new User_Customer((userDtls.getId() + 2)  * 200, userDtls.getUsername() + "_user_"  + 3, userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), "wallet", null, wallet_details);

        userDtls.setCustomer(userCustomerArray);

        User_Admin[] userAdminArray = new User_Admin[1];
        userAdminArray[0] = new User_Admin(101, userDtls.getUsername() + "_admin_"  + 1, userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail());

        userDtls.setAdmin(userAdminArray);

        User_Author[] userAuthorArray = new User_Author[2];
        userAuthorArray[0] = new User_Author(userDtls.getId() * 400, userDtls.getUsername() + "_author_"  + 1, userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), 4, null, null, null, "dummy", null, "dummy.us/zoom", "zoom");
        userAuthorArray[1] = new User_Author((userDtls.getId() + 1) * 400, userDtls.getUsername() + "_author_"  + 2, userDtls.getPassword(), userDtls.getName(), userDtls.getPhone(), userDtls.getHome_address(), userDtls.getShipping_address(), userDtls.getEmail(), 2, null, null, null, "dummy", null, "dummy1.us/zoom", "zoom");

        userDtls.setAuthor(userAuthorArray);

        return userDtls;
    }

    public void forCodeCoverage(){
        UserDetails dummyUser = new UserDetails(1);
        dummyUser.setUsername("dummy");
        dummyUser.setPassword("dummy");
        dummyUser.setName("dummy");
        dummyUser.setPhone(5152459812l);
        dummyUser.setHome_address("dummy");
        dummyUser.setShipping_address("dummy");
        dummyUser.setEmail("dummy@gmail.com");

        dummyUserDetails.getCustomer()[0].getPayment_type();
        dummyUserDetails.getCustomer()[0].getCard_details();
        dummyUserDetails.getCustomer()[0].getWallet_details();

        dummyUserDetails.getAuthor()[0].getRating();
        dummyUserDetails.getAuthor()[0].getReviews();
        dummyUserDetails.getAuthor()[0].getTopics();
        dummyUserDetails.getAuthor()[0].getGrades();
        dummyUserDetails.getAuthor()[0].getAuthor_description();
        dummyUserDetails.getAuthor()[0].getSlots();
        dummyUserDetails.getAuthor()[0].getLink();
        dummyUserDetails.getAuthor()[0].getPlatform();
    }

    @Test
    public void testAuthenticateAdmin() throws IOException  {
        // Setup
        String userName = dummyUserDetails.getAdmin()[0].getUsername();
        String password = dummyUserDetails.getAdmin()[0].getPassword();
        // Invoke
        Boolean response = userFileDAO.authenticateAdmin(userName, password);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }

    @Test
    public void testAuthenticateAdmin_InvalidCredentials() throws IOException  {
        // Setup
        String userName = dummyUserDetails.getAdmin()[0].getUsername() + "_invalid";
        String password = dummyUserDetails.getAdmin()[0].getPassword();
        // Invoke
        Boolean response = userFileDAO.authenticateAdmin(userName, password);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }

    @Test
    public void testAuthenticateCustomer() throws IOException  {
        // Setup
        String userName = dummyUserDetails.getCustomer()[0].getUsername();
        String password = dummyUserDetails.getCustomer()[0].getPassword();
        // Invoke
        Boolean response = userFileDAO.authenticateCustomer(userName, password);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }

    @Test
    public void testAuthenticateCustomer_InvalidCredentials() throws IOException  {
        // Setup
        String userName = dummyUserDetails.getCustomer()[0].getUsername() + "_invalid";
        String password = dummyUserDetails.getCustomer()[0].getPassword();
        // Invoke
        Boolean response = userFileDAO.authenticateCustomer(userName, password);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }
    
    @Test
    public void testAuthenticateAuthor() throws IOException  {
        // Setup
        String userName = dummyUserDetails.getAuthor()[0].getUsername();
        String password = dummyUserDetails.getAuthor()[0].getPassword();
        // Invoke
        Boolean response = userFileDAO.authenticateAuthor(userName, password);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }

    @Test
    public void testAuthenticateAuthor_InvalidCredentials() throws IOException  {
        // Setup
        String userName = dummyUserDetails.getAuthor()[0].getUsername() + "_invalid";
        String password = dummyUserDetails.getAuthor()[0].getPassword();
        // Invoke
        Boolean response = userFileDAO.authenticateAuthor(userName, password);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }
    
    @Test
    public void testAuthenticateAdminUsingUserId() throws IOException  {
        // Setup
        int userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateAdmin(userId);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }

    @Test
    public void testAuthenticateAdminUsingUserId_InvalidCredentials() throws IOException  {
         // Setup
         int userId = dummyUserDetails.getAdmin()[0].getId() * 1000;
         // Invoke
         Boolean response = userFileDAO.authenticateAdmin(userId);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }

    @Test
    public void testAuthenticateCustomerUsingUserId() throws IOException  {
        // Setup
        int userId = dummyUserDetails.getCustomer()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateCustomer(userId);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }

    @Test
    public void testAuthenticateCustomerUsingUserId_InvalidCredentials() throws IOException  {
        // Setup
        int userId = dummyUserDetails.getCustomer()[0].getId() * 1000;
        // Invoke
        Boolean response = userFileDAO.authenticateCustomer(userId);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }
    
    @Test
    public void testAuthenticateAuthorUsingUserId() throws IOException  {
        // Setup
        int userId = dummyUserDetails.getAuthor()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateAuthor(userId);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }

    @Test
    public void testAuthenticateAuthorUsingUserId_InvalidCredentials() throws IOException  {
        // Setup
        int userId = dummyUserDetails.getAuthor()[0].getId() * 1000;
        // Invoke
        Boolean response = userFileDAO.authenticateAuthor(userId);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }

    @Test
    public void testGetUserData_Customer(){
        // Setup
        String userName = dummyUserDetails.getCustomer()[0].getUsername();
        String password = dummyUserDetails.getCustomer()[0].getPassword();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userName, password);

        // Analyze
        assertEquals(dummyUserDetails.getCustomer()[0],response);
    }

    @Test
    public void testGetUserData_Admin(){
        // Setup
        String userName = dummyUserDetails.getAdmin()[0].getUsername();
        String password = dummyUserDetails.getAdmin()[0].getPassword();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userName, password);

        // Analyze
        assertEquals(dummyUserDetails.getAdmin()[0],response);
    }

    @Test
    public void testGetUserData_Author(){
        // Setup
        String userName = dummyUserDetails.getAuthor()[0].getUsername();
        String password = dummyUserDetails.getAuthor()[0].getPassword();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userName, password);

        // Analyze
        assertEquals(dummyUserDetails.getAuthor()[0],response);
    }

    @Test
    public void testGetUserData_Null(){
        // Setup
        String userName = dummyUserDetails.getCustomer()[0].getUsername() + "_invalid";
        String password = dummyUserDetails.getCustomer()[0].getPassword();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userName, password);

        // Analyze
        assertEquals(null,response);
    }

    @Test
    public void testGetUserDataUsingUserId_Customer(){
        // Setup
        int userId = dummyUserDetails.getCustomer()[0].getId();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userId);

        // Analyze
        assertEquals(dummyUserDetails.getCustomer()[0],response);
    }

    @Test
    public void testGetUserDataUsingUserId_Admin(){
        // Setup
        int userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userId);

        // Analyze
        assertEquals(dummyUserDetails.getAdmin()[0],response);
    }

    @Test
    public void testGetUserDataUsingUserId_Author(){
        // Setup
        int userId = dummyUserDetails.getAuthor()[0].getId();
        // Invoke
        UserDetails response = userFileDAO.getUserData(userId);

        // Analyze
        assertEquals(dummyUserDetails.getAuthor()[0],response);
    }

    @Test
    public void testGetUserDataUsingUserId_Null(){
        // Setup
        int userId = dummyUserDetails.getAuthor()[0].getId() * 1000;
        // Invoke
        UserDetails response = userFileDAO.getUserData(userId);

        // Analyze
        assertEquals(null,response);
    }

    @Test
    public void testAuthenticateGuest(){
        // Setup
        String userId = "guest";
        // Invoke
        Boolean response = userFileDAO.authenticateGuest(userId);

        // Analyze
        assertEquals(Boolean.TRUE,response);
    }
    
    @Test
    public void testAuthenticateGuest_Invalid(){
        // Setup
        String userId = null;
        // Invoke
        Boolean response = userFileDAO.authenticateGuest(userId);

        // Analyze
        assertEquals(Boolean.FALSE,response);
    }

    @Test
    public void testAuthenticateUser_All_Admin(){
        // Setup
        Object userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 0);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

    @Test
    public void testAuthenticateUser_All_Guest(){
        // Setup
        Object userId = "guest";
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 0);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

    @Test
    public void testAuthenticateUser_All_Customer(){
        // Setup
        Object userId = dummyUserDetails.getCustomer()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 0);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

    @Test
    public void testAuthenticateUser_All_Author(){
        // Setup
        Object userId = dummyUserDetails.getAuthor()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 0);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }
    
    @Test
    public void testAuthenticateUser_Admin(){
        // Setup
        Object userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 1);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

        
    @Test
    public void testAuthenticateUser_Admin_Invalid(){
        // Setup
        Object userId = dummyUserDetails.getCustomer()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 1);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }

    @Test
    public void testAuthenticateUser_Admin_GuestError(){
        // Setup
        Object userId = "guest";
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 1);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }
        
    @Test
    public void testAuthenticateUser_Author(){
        // Setup
        Object userId = dummyUserDetails.getAuthor()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 2);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

        
    @Test
    public void testAuthenticateUser_Author_Invalid(){
        // Setup
        Object userId = dummyUserDetails.getCustomer()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 2);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }

    @Test
    public void testAuthenticateUser_Author_GuestError(){
        // Setup
        Object userId = "guest";
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 2);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }
        
    @Test
    public void testAuthenticateUser_Customer(){
        // Setup
        Object userId = dummyUserDetails.getCustomer()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 3);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

        
    @Test
    public void testAuthenticateUser_Customer_Invalid(){
        // Setup
        Object userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 3);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }

    @Test
    public void testAuthenticateUser_Customer_GuestError(){
        // Setup
        Object userId = "guest";
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 3);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }
        
    @Test
    public void testAuthenticateUser_Guest(){
        // Setup
        Object userId = "guest";
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 4);

        // Analyze
        assertEquals(Boolean.TRUE,response);        
    }

        
    @Test
    public void testAuthenticateUser_Guest_Invalid(){
        // Setup
        Object userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 4);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }

    @Test
    public void testAuthenticateUser_InvalidType(){
        // Setup
        Object userId = dummyUserDetails.getAdmin()[0].getId();
        // Invoke
        Boolean response = userFileDAO.authenticateUser(userId, 5);

        // Analyze
        assertEquals(Boolean.FALSE,response);        
    }
}
