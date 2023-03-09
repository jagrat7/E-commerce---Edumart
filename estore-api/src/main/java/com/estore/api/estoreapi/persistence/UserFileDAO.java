package com.estore.api.estoreapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.estore.api.estoreapi.model.UserDetails;
import com.estore.api.estoreapi.model.User_Admin;
import com.estore.api.estoreapi.model.User_Author;
import com.estore.api.estoreapi.model.User_Customer;
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserFileDAO implements UserDAO {

    private ObjectMapper objectMapper;
    private UserDetails userDetails;
    private String userFileName;
    HashMap<Integer, User_Admin> userIdVsAdmin;
    HashMap<Integer, User_Customer> userIdVsCustomer;
    HashMap<Integer, User_Author> userIdVsAuthor;
    int nextCustomerId = 1;

    
    public UserFileDAO(@Value("${user.file}") String userFileName, ObjectMapper objectMapper) throws IOException{
        this.objectMapper = objectMapper;
        this.userFileName = userFileName;
        userDetails = objectMapper.readValue(new File(userFileName), UserDetails.class);
        initializeData();
    }

    public void initializeData(){
        userIdVsAdmin = new HashMap<>();
        userIdVsCustomer = new HashMap<>();
        userIdVsAuthor = new HashMap<>();

        for(User_Admin admin_data : userDetails.getAdmin()){
            userIdVsAdmin.put(admin_data.getId(), admin_data);
        }

        for(User_Customer customer_data : userDetails.getCustomer()){
            userIdVsCustomer.put(customer_data.getId(), customer_data);
            nextCustomerId = customer_data.getId() + 1;
        }

        for(User_Author author_data : userDetails.getAuthor()){
            userIdVsAuthor.put(author_data.getId(), author_data);
        }
    }

    private void saveUser() throws IOException {
        userDetails = getAllUserData();
        objectMapper.writeValue(new File(userFileName), userDetails);
    }

    private UserDetails getAllUserData(){
        UserDetails userDtls = new UserDetails();
        List<User_Customer> customer = new ArrayList<>();
        List<User_Admin> admin = new ArrayList<>();
        List<User_Author> author = new ArrayList<>();

        for(Integer userId : userIdVsAdmin.keySet()){
            admin.add(userIdVsAdmin.get(userId));
        }

        for(Integer userId : userIdVsAuthor.keySet()){
            author.add(userIdVsAuthor.get(userId));
        }

        for(Integer userId : userIdVsCustomer.keySet()){
            customer.add(userIdVsCustomer.get(userId));
        }

        User_Customer[] userCustomer = new User_Customer[customer.size()];
        customer.toArray(userCustomer);

        User_Admin[] userAdmin = new User_Admin[admin.size()];
        admin.toArray(userAdmin);

        User_Author[] userAuthor = new User_Author[author.size()];
        author.toArray(userAuthor);

        userDtls.setAdmin(userAdmin);
        userDtls.setAuthor(userAuthor);
        userDtls.setCustomer(userCustomer);

        return userDtls;
    }

    @Override
    public Boolean authenticateAdmin(String username, String password) {
        for(User_Admin admin_data : userDetails.getAdmin()){
            if(admin_data.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean authenticateCustomer(String username, String password) {
        for(User_Customer customer_data : userDetails.getCustomer()){
            if(customer_data.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean authenticateAuthor(String username, String password) {
        for(User_Author author_data : userDetails.getAuthor()){
            if(author_data.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean authenticateAdmin(int userId) {
        return userIdVsAdmin.containsKey(userId);
    }

    @Override
    public Boolean authenticateCustomer(int userId) {
        return userIdVsCustomer.containsKey(userId);
    }

    @Override
    public Boolean authenticateAuthor(int userId) {
        return userIdVsAuthor.containsKey(userId);
    }

    @Override
    public UserDetails getUserData(String username, String password) {
        for(User_Admin admin_data : userDetails.getAdmin()){
            if(admin_data.getUsername().equals(username)){
                return admin_data;
            }
        }
        for(User_Customer customer_data : userDetails.getCustomer()){
            if(customer_data.getUsername().equals(username)){
                return customer_data;
            }
        }
        for(User_Author author_data : userDetails.getAuthor()){
            if(author_data.getUsername().equals(username)){
                return author_data;
            }
        }
        return null;
    }

    @Override
    public UserDetails getUserData(int userId) {
        if(Boolean.TRUE.equals(authenticateAdmin(userId))){
            return userIdVsAdmin.get(userId);
        }else if(Boolean.TRUE.equals(authenticateAuthor(userId))){
            return userIdVsAuthor.get(userId);
        }else if(Boolean.TRUE.equals(authenticateCustomer(userId))){
            return userIdVsCustomer.get(userId);
        }
        return null;
    }

    
    @Override
    public UserDetails getUserData(int userId, int userTypeNeeded) {
        if(Boolean.TRUE.equals(authenticateAdmin(userId)) && (userTypeNeeded == 0 || userTypeNeeded == 1)){
            return userIdVsAdmin.get(userId);
        }else if(Boolean.TRUE.equals(authenticateAuthor(userId)) && (userTypeNeeded == 0 || userTypeNeeded == 2)){
            return userIdVsAuthor.get(userId);
        }else if(Boolean.TRUE.equals(authenticateCustomer(userId)) && (userTypeNeeded == 0 || userTypeNeeded == 3)){
            return userIdVsCustomer.get(userId);
        }
        return null;
    }

    @Override
    public Boolean authenticateGuest(String userId){
        return userId != null && userId.equalsIgnoreCase(CodeSmellFixer.LowerCase.GUEST);
    }

    @Override
    public Boolean authenticateUser(Object userId, int userTypeNeeded){
        Integer userIdInt = null;
        Boolean isAdmin; 
        Boolean isCustomer;
        Boolean isAuthor;
        switch(userTypeNeeded){
            case 0:
            if(Boolean.FALSE.equals(isNumeric(userId))){
                return authenticateGuest(userId.toString());
            }
            userIdInt = Integer.parseInt(userId.toString());
            isAdmin = authenticateAdmin(userIdInt);
            isCustomer = authenticateCustomer(userIdInt);
            isAuthor = authenticateAuthor(userIdInt);
            return isAdmin || isCustomer || isAuthor;
            case 1:
            if(Boolean.FALSE.equals(isNumeric(userId))){
                return false;
            }
            userIdInt = Integer.parseInt(userId.toString());
            isAdmin = authenticateAdmin(userIdInt);
            return isAdmin;
            case 2:
            if(Boolean.FALSE.equals(isNumeric(userId))){
                return false;
            }
            userIdInt = Integer.parseInt(userId.toString());
            isAuthor = authenticateAuthor(userIdInt);
            return isAuthor;
            case 3:
            if(Boolean.FALSE.equals(isNumeric(userId))){
                return false;
            }
            userIdInt = Integer.parseInt(userId.toString());
            isCustomer = authenticateCustomer(userIdInt);
            return isCustomer;
            default:
            return authenticateGuest(userId.toString());
        }
    }
    private Boolean isNumeric(Object userId){
        try{
            Integer.parseInt(userId.toString());
            return true;
        }catch(Exception ex){
            return false;
        }
    }
    
    @Override
    public boolean registerCustomer(String username, String password) throws Exception{
        if(getUserData(username,password) == null){
            userIdVsCustomer.put(nextCustomerId, new User_Customer(nextCustomerId, username, password, username, 0, null, null, null, null, null, null));
            saveUser();
            return true;
        }else{
            return false;
        }
    }
}
