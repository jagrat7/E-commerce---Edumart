package com.estore.api.estoreapi.persistence;

import com.estore.api.estoreapi.model.UICartProducts;
import com.estore.api.estoreapi.model.UICartSessions;

import java.util.HashMap;

public interface CartDAO {
        UICartProducts[] getProductsInCartForUser(int userId) throws Exception;

        HashMap<String, Object> getCartProductsAndSessions(int userId) throws Exception;

        Boolean checkProductsAvailableInOverallCart(int productId, String type) throws Exception;

        Boolean checkProductsAvailableInCartForUser(int userId, int productId, String type) throws Exception;

        Boolean checkSessionsAvailableInCartForUser(int userId, int time, String authorName) throws Exception;

        UICartProducts increaseProductInCartForUser(int userId, int productId, String type, int incrementQuantity)
                        throws Exception;

        UICartProducts decreaseProductInCartForUser(int userId, int productId, String type, int decrementQuantity)
                        throws Exception;

        UICartProducts[] addProductsInCart(int userId, int productId, String type, int quantity)
                        throws Exception;

        UICartSessions[] addSessionsInCart(int userId, int time, String authorName) throws Exception;

        UICartProducts[] removeProductsInCart(int userId, int productId, String type) throws Exception;

        UICartSessions[] removeSessionsInCart(int userId, int time, String authorName) throws Exception;

        Boolean checkoutCart(int userId) throws Exception;
}
