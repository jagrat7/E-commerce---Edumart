package com.estore.api.estoreapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.estore.api.estoreapi.model.Cart;
import com.estore.api.estoreapi.model.CartProducts;
import com.estore.api.estoreapi.model.CartSessions;
import com.estore.api.estoreapi.model.Product;
import com.estore.api.estoreapi.model.UICartProducts;
import com.estore.api.estoreapi.model.UICartSessions;
import com.estore.api.estoreapi.model.UserDetails;
import com.estore.api.estoreapi.model.User_Author;
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CartFileDAO implements CartDAO {

    private ObjectMapper objectMapper;
    private HashMap<Integer, Cart> userIdVsCart;
    private Cart[] cartDetails;
    private String cartFileName;
    private ProductFileDAO productFileDAO;
    private UserFileDAO userFileDAO;
    private HistoryDAO historyDAO;
    
    public CartFileDAO(@Value("${cart.file}") String cartFileName, ObjectMapper objectMapper, ProductFileDAO productFileDAO, HistoryDAO historyDAO, UserFileDAO userDAO) throws Exception{
        this.objectMapper = objectMapper;
        this.cartFileName = cartFileName;
        this.userFileDAO = userDAO;
        this.productFileDAO = productFileDAO;
        this.historyDAO = historyDAO;
        cartDetails = objectMapper.readValue(new File(cartFileName), Cart[].class);
        setUserIdVsCart();
    }

    public void setUserIdVsCart() {
        userIdVsCart = new HashMap<>();
        for (Cart cart : cartDetails) {
            if (userIdVsCart.containsKey(cart.getUser_id())) {
                Cart oldCart = userIdVsCart.get(cart.getUser_id());

                CartProducts[] cartProducts = oldCart.getProducts();
                CartProducts[] newCartProducts = cart.getProducts();
                CartProducts[] updatedCartProducts = Arrays.copyOf(cartProducts,
                        cartProducts.length + newCartProducts.length);
                System.arraycopy(newCartProducts, 0, updatedCartProducts, cartProducts.length, newCartProducts.length);

                CartSessions[] cartSessions = oldCart.getSessions();
                CartSessions[] newCartSessions = cart.getSessions();
                CartSessions[] updatedCartSessions = Arrays.copyOf(cartSessions,
                        cartSessions.length + newCartSessions.length);
                System.arraycopy(newCartSessions, 0, updatedCartSessions, cartSessions.length, newCartSessions.length);

                oldCart = new Cart(cart.getUser_id(), updatedCartProducts, newCartSessions);
                userIdVsCart.put(cart.getUser_id(), oldCart);
            } else {
                userIdVsCart.put(cart.getUser_id(), cart);
            }
        }
    }

    private UICartProducts[] getProductInfo(CartProducts[] cartProducts) throws Exception {
        List<UICartProducts> uiCartList = new ArrayList<>();
        for (CartProducts currentProduct : cartProducts) {
            Product product = productFileDAO.getProduct(currentProduct.getId(), currentProduct.getType(), true);
            if (product == null) {
                continue; // Delete Product in cart when deleted in products json
            }
            Object productData = null;
            String type = null;
            if (currentProduct.getType().equals("video")) {
                productData = product.getVideos()[0];
                type = "video";
            } else if (currentProduct.getType().equals("book")) {
                productData = product.getBooks()[0];
                type = "book";
            } else {
                productData = product.getAudios()[0];
                type = "audio";
            }
            uiCartList.add(new UICartProducts(currentProduct.getQuantity(), type, productData));
        }
        UICartProducts[] uiCartProductsArray = new UICartProducts[uiCartList.size()];
        uiCartList.toArray(uiCartProductsArray);
        return uiCartProductsArray;
    }

    private UICartSessions[] getSessionInfo(CartSessions[] cartSessions) {
        List<UICartSessions> uiCartList = new ArrayList<>();
        for (CartSessions currentSession : cartSessions) {
            uiCartList.add(
                    new UICartSessions(currentSession.getTime(), currentSession.getName(), currentSession.getStatus()));
        }
        UICartSessions[] uiCartSessionsArray = new UICartSessions[uiCartList.size()];
        uiCartList.toArray(uiCartSessionsArray);
        return uiCartSessionsArray;
    }

    private void saveCart() throws IOException {
        objectMapper.writeValue(new File(cartFileName), getCartArray());
    }

    private Cart[] getCartArray() {
        Iterator<Integer> userIds = userIdVsCart.keySet().iterator();
        List<Cart> cartList = new ArrayList<>();
        while (userIds.hasNext()) {
            Integer userId = userIds.next();
            cartList.add(userIdVsCart.get(userId));
        }
        Cart[] cartArray = new Cart[cartList.size()];
        cartList.toArray(cartArray);
        return cartArray;
    }

    @Override
    public UICartProducts[] getProductsInCartForUser(int userId) throws Exception {
        return userIdVsCart.containsKey(userId) ? getProductInfo(userIdVsCart.get(userId).getProducts()) : null;
    }

    @Override
    public HashMap<String, Object> getCartProductsAndSessions(int userId) throws Exception {
        UICartProducts[] products = userIdVsCart.containsKey(userId)
                ? getProductInfo(userIdVsCart.get(userId).getProducts())
                : null;
        UICartSessions[] sessions = userIdVsCart.containsKey(userId)
                ? getSessionInfo(userIdVsCart.get(userId).getSessions())
                : null;

        HashMap<String, Object> returnMap = new HashMap<>();

        if (products != null) {
            returnMap.put(CodeSmellFixer.LowerCase.PRODUCTS, products);
        } else {
            returnMap.put(CodeSmellFixer.LowerCase.PRODUCTS, new UICartProducts[0]);
        }

        if (sessions != null) {
            returnMap.put(CodeSmellFixer.LowerCase.SESSIONS, sessions);
        } else {
            returnMap.put(CodeSmellFixer.LowerCase.SESSIONS, new UICartSessions[0]);
        }

        return returnMap;
    }

    @Override
    public Boolean checkProductsAvailableInCartForUser(int userId, int productId, String type) throws Exception {
        productFileDAO.isGivenTypeAllowedExceptionCheck(type);
        if (userIdVsCart.containsKey(userId)) {
            CartProducts[] cartProducts = userIdVsCart.get(userId).getProducts();
            for (CartProducts currentProduct : cartProducts) {
                if (currentProduct.getId() == productId && currentProduct.getType().equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean checkSessionsAvailableInCartForUser(int userId, int time, String authorName) throws Exception {
        if (userIdVsCart.containsKey(userId)) {
            CartSessions[] cartSessions = userIdVsCart.get(userId).getSessions();
            if (cartSessions.length == 0) {
                return false;
            }
            for (CartSessions currentSession : cartSessions) {
                if (currentSession.getName().equals(authorName)  && currentSession.getTime() == time) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean checkProductsAvailableInOverallCart(int productId, String type) throws Exception {
        productFileDAO.isGivenTypeAllowedExceptionCheck(type);
        for(int user_id : userIdVsCart.keySet()) {
            CartProducts[] cartProducts = userIdVsCart.get(user_id).getProducts();
            for (CartProducts currentProduct : cartProducts) {
                if (currentProduct.getId() == productId && currentProduct.getType().equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public UICartProducts increaseProductInCartForUser(int userId, int productId, String type, int incrementQuantity)
            throws Exception {
        productFileDAO.isGivenTypeAllowedExceptionCheck(type);
        CartProducts resultCartProducts;
        if (userIdVsCart.containsKey(userId)) {
            Cart userCart = userIdVsCart.get(userId);
            CartProducts[] cartProducts = userCart.getProducts();
            CartProducts currentCart = new CartProducts(type, productId, incrementQuantity);

            Boolean isProductFound = false;

            for (CartProducts currentProduct : cartProducts) {
                if (currentProduct.getId() == productId && currentProduct.getType().equalsIgnoreCase(type)) {
                    currentProduct.setQuantity(currentProduct.getQuantity() + incrementQuantity);
                    isProductFound = true;
                    currentCart = currentProduct;
                    break;
                }
            }

            if (Boolean.FALSE.equals(isProductFound)) {
                CartProducts[] newCartProducts = new CartProducts[] { currentCart };
                CartProducts[] updatedCartProducts = Arrays.copyOf(cartProducts,
                        cartProducts.length + newCartProducts.length);
                System.arraycopy(newCartProducts, 0, updatedCartProducts, cartProducts.length, newCartProducts.length);
                cartProducts = updatedCartProducts;
            }

            userCart.setProducts(cartProducts);
            userIdVsCart.put(userId, userCart);
            resultCartProducts = currentCart;
        } else {
            if (productFileDAO.isGivenIdPresent(productId, type)) {
                CartProducts cartProducts = new CartProducts(type, productId, incrementQuantity);
                Cart cart = new Cart(userId, new CartProducts[] { cartProducts }, new CartSessions[] {});
                userIdVsCart.put(userId, cart);
                resultCartProducts = cartProducts;
            } else {
                return null;
            }
        }
        saveCart();
        return getProductInfo(new CartProducts[] { resultCartProducts })[0];
    }

    @Override
    public UICartProducts decreaseProductInCartForUser(int userId, int productId, String type, int decrementQuantity)
            throws Exception {
        productFileDAO.isGivenTypeAllowedExceptionCheck(type);
        CartProducts resultCartProducts;
        if (userIdVsCart.containsKey(userId)) {
            Cart userCart = userIdVsCart.get(userId);
            CartProducts[] cartProducts = userCart.getProducts();
            CartProducts currentCart = new CartProducts(type, productId, 0);
            CartProducts toBeRemovedCartIfZeroQuantity = null;

            Boolean isProductFound = false;

            List<CartProducts> cartProductsList = new ArrayList<>();
            cartProductsList.addAll(Arrays.asList(cartProducts));

            for (CartProducts currentProduct : cartProductsList) {
                if (currentProduct.getId() == productId && currentProduct.getType().equalsIgnoreCase(type)) {
                    int newQuantity = currentProduct.getQuantity() - decrementQuantity;
                    currentCart = currentProduct;
                    if (newQuantity <= 0) {
                        toBeRemovedCartIfZeroQuantity = currentProduct;
                    }
                    currentProduct.setQuantity(newQuantity);
                    isProductFound = true;
                    break;
                }
            }

            if (null != toBeRemovedCartIfZeroQuantity) {
                cartProductsList.remove(toBeRemovedCartIfZeroQuantity);
            }

            if (Boolean.FALSE.equals(isProductFound)) {
                CartProducts[] newCartProducts = new CartProducts[] { currentCart };
                CartProducts[] updatedCartProducts = Arrays.copyOf(cartProducts,
                        cartProducts.length + newCartProducts.length);
                System.arraycopy(newCartProducts, 0, updatedCartProducts, cartProducts.length, newCartProducts.length);
                cartProducts = updatedCartProducts;
            } else {
                cartProducts = new CartProducts[cartProductsList.size()];
                cartProductsList.toArray(cartProducts);
            }

            userCart.setProducts(cartProducts);
            userIdVsCart.put(userId, userCart);
            resultCartProducts = currentCart;
        } else {
            if (productFileDAO.isGivenIdPresent(productId, type)) {
                CartProducts cartProducts = new CartProducts(type, productId, 0);
                Cart cart = new Cart(userId, new CartProducts[] { cartProducts }, new CartSessions[] {});
                userIdVsCart.put(userId, cart);
                resultCartProducts = cartProducts;
            } else {
                return null;
            }
        }
        saveCart();
        if (Boolean.FALSE.equals(checkProductsAvailableInOverallCart(productId, type))) {
            productFileDAO.deleteProduct(productId, type, true);
        }
        return getProductInfo(new CartProducts[] { resultCartProducts })[0];
    }

    @Override
    public UICartProducts[] addProductsInCart(int userId, int productId, String type, int quantity) throws Exception {
        productFileDAO.isGivenTypeAllowedExceptionCheck(type);
        CartProducts[] resultCartProducts = null;
        if (Boolean.TRUE.equals(checkProductsAvailableInCartForUser(userId, productId, type))) {
            throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_CART);
        }
        if (productFileDAO.isGivenIdPresent(productId, type)) {
            CartProducts cartProducts = new CartProducts(type, productId, quantity);
            CartProducts[] cartProductsArray = null;
            CartSessions[] cartSessionsArray = null;
            if (userIdVsCart.containsKey(userId)) {
                CartProducts[] newCartProducts = new CartProducts[] { cartProducts };
                CartProducts[] oldCartProducts = userIdVsCart.get(userId).getProducts();
                cartSessionsArray = userIdVsCart.get(userId).getSessions();
                cartProductsArray = Arrays.copyOf(oldCartProducts, oldCartProducts.length + newCartProducts.length);
                System.arraycopy(newCartProducts, 0, cartProductsArray, oldCartProducts.length, newCartProducts.length);
            } else {
                cartProductsArray = new CartProducts[] { cartProducts };
                cartSessionsArray = new CartSessions[] {};
            }
            Cart cart = new Cart(userId, cartProductsArray, cartSessionsArray);
            userIdVsCart.put(userId, cart);
            resultCartProducts = cartProductsArray;
        } else {
            return null;
        }
        saveCart();
        return getProductInfo(resultCartProducts);
    }

    private String isGivenSessionPresent(String authorName, int time) {
        // if present live else not_live
        UserDetails details = userFileDAO.getUserData(authorName, authorName);
        int id = details.getId();
        User_Author details2 = (User_Author) userFileDAO.getUserData(id, 2);
        String status = CodeSmellFixer.SnakeCase.NOT_LIVE;
        if (details2.getUsername().equals(authorName)) {
            List<Long> timeSlots = details2.getSlots();
            if (timeSlots.contains((long) time)) {
                status = CodeSmellFixer.LowerCase.LIVE;
            }
        }
        return status;
    }

    @Override
    public UICartSessions[] addSessionsInCart(int userId, int time, String authorName) throws Exception {

        CartSessions[] resultCartSessions = null;
        if (Boolean.TRUE.equals(checkSessionsAvailableInCartForUser(userId, time, authorName))) {
            throw new Exception(CodeSmellFixer.UpperCase.SESSION_ALREADY_IN_CART);
        }
        String status = isGivenSessionPresent(authorName, time);
        if (status.equals(CodeSmellFixer.LowerCase.LIVE)) {
            CartSessions cartSessions = new CartSessions(authorName, time, CodeSmellFixer.LowerCase.LIVE);
            CartProducts[] cartProductsArray = null;
            CartSessions[] cartSessionsArray = null;
            if (userIdVsCart.containsKey(userId)) {
                CartSessions[] newCartSessions = new CartSessions[] { cartSessions };
                CartSessions[] oldCartSessions = userIdVsCart.get(userId).getSessions();
                cartProductsArray = userIdVsCart.get(userId).getProducts();
                cartSessionsArray = Arrays.copyOf(oldCartSessions, oldCartSessions.length + newCartSessions.length);
                System.arraycopy(newCartSessions, 0, cartSessionsArray, oldCartSessions.length, newCartSessions.length);
            } else {
                cartProductsArray = new CartProducts[] {};
                cartSessionsArray = new CartSessions[] { cartSessions };
            }
            Cart cart = new Cart(userId, cartProductsArray, cartSessionsArray);
            userIdVsCart.put(userId, cart);
            resultCartSessions = cartSessionsArray;
        } else {
            return null;
        }
        saveCart();
        return getSessionInfo(resultCartSessions);
    }

    @Override
    public UICartProducts[] removeProductsInCart(int userId, int productId, String type) throws Exception {
        if (!productFileDAO.isGivenTypeAllowed(type)) {
            throw new Exception("TYPE_NOT_FOUND");
        }
        if (userIdVsCart.containsKey(userId)) {
            Cart userCart = userIdVsCart.get(userId);
            CartProducts[] cartProducts = userCart.getProducts();
            List<CartProducts> updatedCartProducts = new ArrayList<>();
            Boolean isProductFound = false;

            for (CartProducts currentProduct : cartProducts) {
                if (!(currentProduct.getId() == productId && currentProduct.getType().equalsIgnoreCase(type))) {
                    updatedCartProducts.add(currentProduct);
                } else {
                    isProductFound = true;
                }
            }
            if (Boolean.FALSE.equals(isProductFound)) {
                return null;
            }
            cartProducts = new CartProducts[updatedCartProducts.size()];
            updatedCartProducts.toArray(cartProducts);

            userCart.setProducts(cartProducts);
            userIdVsCart.put(userId, userCart);
            saveCart();
            if (Boolean.FALSE.equals(checkProductsAvailableInOverallCart(productId, type))) {
                productFileDAO.deleteProduct(productId, type, true);
            }
            return getProductInfo(cartProducts);

        } else {
            return null;
        }
    }

    @Override
    public UICartSessions[] removeSessionsInCart(int userId, int time, String authorName) throws Exception {
        if (userIdVsCart.containsKey(userId)) {
            Cart userCart = userIdVsCart.get(userId);
            CartSessions[] cartSessions = userCart.getSessions();
            List<CartSessions> updatedCartSessions = new ArrayList<>();
            Boolean isSessionFound = false;
            for (CartSessions cartSession : cartSessions) {
                if (!(cartSession.getTime() == time && cartSession.getName().equalsIgnoreCase(authorName))) {
                    updatedCartSessions.add(cartSession);
                } else {
                    isSessionFound = true;
                }
            }
            if (Boolean.FALSE.equals(isSessionFound)) {
                return null;
            }
            cartSessions = new CartSessions[updatedCartSessions.size()];
            updatedCartSessions.toArray(cartSessions);

            userCart.setSessions(cartSessions);
            userIdVsCart.put(userId, userCart);
            saveCart();

            return getSessionInfo(cartSessions);

        } else {
            return null;
        }
    }

    @Override
    public Boolean checkoutCart(int userId) throws Exception {
        if (userIdVsCart.containsKey(userId)) {
            Cart userCart = userIdVsCart.get(userId);
            CartProducts[] cartProducts = userCart.getProducts();
            CartSessions[] cartSessions = userCart.getSessions();
            for (CartProducts currentProduct : cartProducts) {
                if (productFileDAO.getProduct(currentProduct.getId(), currentProduct.getType()) == null) {
                    return false;
                }
            }
            for (CartSessions currentSession : cartSessions) {
                if (isGivenSessionPresent(currentSession.getName(), currentSession.getTime()).equals(CodeSmellFixer.SnakeCase.NOT_LIVE)) {
                    return false;
                }
            }

            userCart.setSessions(new CartSessions[0]);
            userCart.setProducts(new CartProducts[0]);
            userIdVsCart.put(userId, userCart);
            saveCart();
            historyDAO.addPurchaseHistoryForCustomer(userId, System.currentTimeMillis(), getProductInfo(cartProducts), getSessionInfo(cartSessions));
            return true;

        } else {
            return null;
        }
    }
}
