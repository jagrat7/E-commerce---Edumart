package com.estore.api.estoreapi.persistence;

import com.estore.api.estoreapi.model.UserDetails;

public interface UserDAO {
    Boolean authenticateAdmin(String username, String password);
    Boolean authenticateCustomer(String username, String password);
    Boolean authenticateAuthor(String username, String password);
    Boolean authenticateAdmin(int userId);
    Boolean authenticateCustomer(int userId);
    Boolean authenticateAuthor(int userId);
    Boolean authenticateGuest(String userId);
    UserDetails getUserData(String username, String password);
    UserDetails getUserData(int userId);
    /*
     * user_type_needed 
     * 0 - any user
     * 1 - admin
     * 2 - author
     * 3 - customer
     * 4 - guest
     */
    Boolean authenticateUser(Object userId, int userTypeNeeded);
    UserDetails getUserData(int userId, int userTypeNeeded);
    boolean registerCustomer(String username, String password) throws Exception;
}
