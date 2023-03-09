package com.estore.api.estoreapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.estore.api.estoreapi.persistence.CartDAO;
import com.estore.api.estoreapi.persistence.HistoryDAO;
import com.estore.api.estoreapi.persistence.ProductDAO;
import com.estore.api.estoreapi.persistence.UserDAO;

import com.estore.api.estoreapi.util.CodeSmellFixer;

import com.estore.api.estoreapi.model.*;
import com.estore.api.estoreapi.model.Product.Image;
import com.estore.api.estoreapi.model.Product.Image.imageType;
import com.estore.api.estoreapi.model.InventoryHistoryActions.Type;

/**
 * Handles the REST API requests for the Hero resource
 * method handler to the Spring framework
 *
 * @author Poorna Chander ,jagrat rao, haotian zang
 * 
 */

@RestController

@RequestMapping("/")
public class StoreController {

    private static final Logger LOG = Logger.getLogger(StoreController.class.getName());
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private UserDAO userDAO;
    private HistoryDAO historyDAO;

    /**
     * Creates a REST API controller to reponds to requests
     *
     * @param productDAO The {@link ProductDAO Product Data Access Object} to
     *                   perform CRUD operations
     *
     *                   This dependency is injected by the Spring Framework
     */
    public StoreController(ProductDAO productDAO, CartDAO cartDAO, UserDAO userDAO, HistoryDAO historyDAO) {
        this.productDAO = productDAO;
        this.cartDAO = cartDAO;
        this.userDAO = userDAO;
        this.historyDAO = historyDAO;
    }

    /**
     * Responds to the GET request for all {@linkplain Product products}
     * Products include Book books and Video videos which are specialisations
     * 
     * @return ResponseEntity with array of {@link Product product} objects (may be
     *         empty) and
     *         HTTP status of OK<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/inventory/products")
    public ResponseEntity<Product> getAllProducts(@RequestHeader Object userId,
            @RequestParam(required = false) String filter, @RequestParam(required = false) String productType) {
        LOG.info("GET /Products");
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 0);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Boolean isTobeAddedDataOnlyNeeded = false;
            if (filter != null) {
                if (!filter.equals("product_requests")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                if (Boolean.FALSE.equals(userDAO.authenticateUser(userId, 1))) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
                isTobeAddedDataOnlyNeeded = true;
            }
            if (productType != null && !Arrays.asList("video", "audio", "book").contains(productType)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Product products = null;
            if (Boolean.TRUE.equals(isTobeAddedDataOnlyNeeded)) {
                products = productDAO.getToBeAddedProducts();
            } else if (productType != null) {
                products = productDAO.getProducts(productType.equals("video"), productType.equals("audio"),
                        productType.equals("book"));
            } else {
                products = productDAO.getProducts();
            }
            if (products != null)
                return new ResponseEntity<>(products, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for a {@linkplain Product product} for the given
     * id
     * 
     * @param id The id used to locate the {@link Product product}
     * 
     * @return ResponseEntity with {@link Product product} object and HTTP status of
     *         OK if found
     *         <br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("inventory/product/{type}/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String type, @PathVariable int id,
            @RequestHeader Object userId) {
        LOG.log(Level.INFO, "GET /Product {0}", id);
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 0);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Product product = productDAO.getProduct(id, type);
            if (product != null)
                return new ResponseEntity<>(product, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * creates a new {@linkplain Product} object with provided product object
     * 
     * @param product the {@linkplain Product} object that is to be created
     * @return ResponseEntity with created {@linkplain Product} object and HTTP
     *         status of <CREATED>.
     *         ResponseEntity with HTTP status of <CONFLICT> if {@link Product}
     *         object already exists.
     *         ResponseEntity with HTTP status of <INTERNAL_SERVER_ERROR> otherwise.
     */
    @SuppressWarnings("unchecked")
    @PostMapping("inventory/product")
    public ResponseEntity<Product> createProduct(@RequestBody HashMap<String, Object> product,
            @RequestHeader Object userId) {
        LOG.log(Level.INFO, "POST /inventory/product {0}", product);
        try {
            Boolean isAuthor = userDAO.authenticateUser(userId, 2);
            Boolean isAdmin = userDAO.authenticateUser(userId, 1);
            Boolean isValidUser = isAdmin || isAuthor;
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (!product.containsKey(CodeSmellFixer.LowerCase.TYPE)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            if (product.containsKey(CodeSmellFixer.LowerCase.IMAGE)) {
                HashMap<String, Object> image = (HashMap<String, Object>) product.get(CodeSmellFixer.LowerCase.IMAGE);
                Boolean isGivenImageLocal = false;
                Image imageData = null;
                if (image.containsKey(CodeSmellFixer.LowerCase.TYPE) && image.containsKey(CodeSmellFixer.CamelCase.IMAGE_SRC)) {
                    isGivenImageLocal = image.get(CodeSmellFixer.LowerCase.TYPE).toString().equalsIgnoreCase(Image.imageType.LOCAL.toString());
                    List<String> imageSrc = (List<String>) image.get(CodeSmellFixer.CamelCase.IMAGE_SRC);
                    if (imageSrc.isEmpty()) {
                        CodeSmellFixer.ExceptionThrower.throwInvalidBody();
                    }
                    imageData = new Image(Image.imageType.valueOf(image.get(CodeSmellFixer.LowerCase.TYPE).toString().toUpperCase()),
                            imageSrc);
                    product.put(CodeSmellFixer.LowerCase.IMAGE, imageData);
                } else {
                    CodeSmellFixer.ExceptionThrower.throwInvalidBody();
                }
                if(Boolean.TRUE.equals(isGivenImageLocal)){
                productDAO.checkIfGivenProductImageIsValid(imageData, product.get(CodeSmellFixer.LowerCase.TYPE).toString(), -1);
                }
            }


            HashMap<String,Object> purchaseLog = product;
            product.put(CodeSmellFixer.SnakeCase.FROM_SRC, Boolean.TRUE.equals(isAdmin) ? CodeSmellFixer.LowerCase.ADMIN : CodeSmellFixer.LowerCase.AUTHOR);
            if (Boolean.TRUE.equals(isAuthor)) {
                product.put(CodeSmellFixer.SnakeCase.REQUEST_TYPE, CodeSmellFixer.SnakeCase.ADD_REQUEST);
                product.put(CodeSmellFixer.LowerCase.AUTHOR, userDAO.getUserData(Integer.parseInt(userId.toString())).getName());
            }
            product.put(CodeSmellFixer.CamelCase.CREATED_BY, Integer.parseInt(userId.toString()));
            Product newProduct=productDAO.createProduct(product);
            if(newProduct!=null){
                if(purchaseLog.containsKey(CodeSmellFixer.SnakeCase.FROM_SRC)){
                    purchaseLog.remove(CodeSmellFixer.SnakeCase.FROM_SRC);
                }
                purchaseLog.put("id", productDAO.getFieldBasedOnType(newProduct, product.get(CodeSmellFixer.LowerCase.TYPE).toString(), CodeSmellFixer.LowerCase.ID));
                historyDAO.addInventoryHistoryForProducts(Integer.parseInt(userId.toString()), Calendar.getInstance().getTimeInMillis(), null, purchaseLog, Type.ADDED);
                return new ResponseEntity<>(newProduct,HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Responds to the GET request for all {@linkplain Products products} whose name
     * contains
     * the text in name and type must be Products to search the Product
     * 
     * @param name The name parameter which contains the text used to find the
     *             {@link Products products}
     * @param type The type parameter which must be Products to fetch the products
     * 
     * @return ResponseEntity with array of {@link Products products} objects (may
     *         be empty) and
     *         HTTP status of OK<br>
     *         ResponseEntity with HTTP status of BAD_REQUEST if type is not of
     *         specified one
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     *         <p>
     *         Example: Find all heroes that contain the text "ma"
     *         GET http://localhost:8080/inventory/search?name=yoga&type=Products
     */
    @GetMapping("inventory/search")
    public ResponseEntity<Product> searchProducts(@RequestParam String name,
            @RequestParam(required = false) String type, @RequestHeader Object userId) {
        LOG.log(Level.INFO, "GET Inventory/Search");
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 0);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Product product;
            String searchType = (type == null) ? CodeSmellFixer.CapitalizationCase.PRODUCTS : type;
            if(searchType.equalsIgnoreCase(CodeSmellFixer.LowerCase.PRODUCTS)){
                product = productDAO.findProducts(name); 
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Product product} with the given id
     * 
     * @param id The id of the {@link Product product} to deleted
     * 
     * @return ResponseEntity HTTP status of OK if deleted<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("inventory/product/{type}/{id}")
    public ResponseEntity<HashMap<String, Object>> deleteProduct(@PathVariable String type, @PathVariable int id,
            @RequestHeader Object userId) {
        LOG.log(Level.INFO, "DELETE /inventory/product/{0}/{1}", new Object[]{type, id});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 1);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Product productInfo = productDAO.getProduct(id,type);
            if(productInfo != null){
                HashMap<String,Object> product = new HashMap<>();
                product.put(CodeSmellFixer.LowerCase.ID, id);
                product.put(CodeSmellFixer.LowerCase.TYPE, type);
                product.put(CodeSmellFixer.LowerCase.NAME, productDAO.getFieldBasedOnType(productInfo, type, CodeSmellFixer.LowerCase.NAME));
                productDAO.deleteProduct(id, type, !cartDAO.checkProductsAvailableInOverallCart(id, type));
                historyDAO.addInventoryHistoryForProducts(Integer.parseInt(userId.toString()), System.currentTimeMillis(), null, product, Type.DELETED);
                HashMap<String,Object> status = new HashMap<>();
                status.put(CodeSmellFixer.LowerCase.ID, id);
                status.put(CodeSmellFixer.LowerCase.STATUS, CodeSmellFixer.LowerCase.DELETED);
                return new ResponseEntity<>(status, HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Updates the {@linkplain Product product} with the provided
     * {@linkplain Product product} object, if it exists
     *
     * @param product The {@link Product product} to update
     *
     * @return ResponseEntity with updated {@link Product product} object and HTTP
     *         status of OK if updated<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @SuppressWarnings("unchecked")
    @PutMapping("inventory/product")
    public ResponseEntity<Product> updateProduct(@RequestBody HashMap<String, Object> product,
            @RequestHeader Object userId, @RequestParam(required = false) String type) {
        LOG.log(Level.INFO, "PUT /products {0}", product);
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 1);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (!product.containsKey(CodeSmellFixer.LowerCase.TYPE) || !product.containsKey(CodeSmellFixer.LowerCase.ID)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            if (product.containsKey(CodeSmellFixer.LowerCase.IMAGE)) {
                HashMap<String, Object> image = (HashMap<String, Object>) product.get(CodeSmellFixer.LowerCase.IMAGE);
                Boolean isGivenImageLocal = false;
                Image imageData = null;
                if (image.containsKey(CodeSmellFixer.LowerCase.TYPE) && image.containsKey(CodeSmellFixer.CamelCase.IMAGE_SRC)) {
                    isGivenImageLocal = image.get(CodeSmellFixer.LowerCase.TYPE).toString().equalsIgnoreCase(Image.imageType.LOCAL.toString());
                    List<String> imageSrc = (List<String>) image.get(CodeSmellFixer.CamelCase.IMAGE_SRC);
                    if (imageSrc.isEmpty()) {
                        CodeSmellFixer.ExceptionThrower.throwInvalidBody();
                    }
                    imageData = new Image(Image.imageType.valueOf(image.get(CodeSmellFixer.LowerCase.TYPE).toString().toUpperCase()),
                            imageSrc);
                    product.put(CodeSmellFixer.LowerCase.IMAGE, imageData);
                } else {
                    CodeSmellFixer.ExceptionThrower.throwInvalidBody();
                }
                if (Boolean.TRUE.equals(isGivenImageLocal)) {
                    productDAO.checkIfGivenProductImageIsValid((Product.Image) product.get(CodeSmellFixer.LowerCase.IMAGE),
                            product.get(CodeSmellFixer.LowerCase.TYPE).toString(), Integer.parseInt(product.get(CodeSmellFixer.LowerCase.ID).toString()));
                } else {
                    productDAO.deleteExistingImages(Integer.parseInt(product.get(CodeSmellFixer.LowerCase.ID).toString()),
                            product.get(CodeSmellFixer.LowerCase.TYPE).toString());
                }
            }

            Product oldProduct = productDAO.getProduct(Integer.parseInt( product.get(CodeSmellFixer.LowerCase.ID).toString() ), product.get(CodeSmellFixer.LowerCase.TYPE).toString());
            
            if(type != null){
                if(!type.equals(CodeSmellFixer.SnakeCase.APPROVE_PRODUCT_REQUEST)){
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                product.put(CodeSmellFixer.SnakeCase.REQUEST_TYPE, CodeSmellFixer.SnakeCase.UPDATE_REQUEST);
            }

            Product updateProduct = productDAO.updateProduct(product);
            if (updateProduct != null){
                HashMap<String,Object> updateProductMap = productDAO.convertProductToHashMapBasedOnType(updateProduct, product.get(CodeSmellFixer.LowerCase.TYPE).toString());
                updateProductMap.put(CodeSmellFixer.LowerCase.TYPE, product.get(CodeSmellFixer.LowerCase.TYPE).toString());
                historyDAO.addInventoryHistoryForProducts(Integer.parseInt(userId.toString()), System.currentTimeMillis(), productDAO.convertProductToHashMapBasedOnType(oldProduct, product.get(CodeSmellFixer.LowerCase.TYPE).toString()), updateProductMap, Type.UPDATED);
                return new ResponseEntity<>(updateProduct, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception ex){
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.PRODUCT_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (null != ex.getMessage() && ex.getMessage().equals(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("inventory/cart")
    public ResponseEntity<HashMap<String, Object>> getAllCartProducts(@RequestHeader Object userId) {
        LOG.log(Level.INFO, "GET /inventory/cart {0}", userId);
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 2) || userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            HashMap<String, Object> cartProducts = cartDAO.getCartProductsAndSessions(Integer.parseInt(userId.toString()));
            if (cartProducts != null)
                return new ResponseEntity<>(cartProducts, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PutMapping("inventory/cart")
    public ResponseEntity<UICartProducts> updateCartProducts(@RequestHeader Object userId,
            @RequestBody HashMap<String, Object> product) {
        LOG.log(Level.INFO, "PUT /inventory/cart {0}, Product = {1}", new Object[]{userId, product});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 2) || userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            UICartProducts cartProduct;
            if (!product.containsKey(CodeSmellFixer.LowerCase.ID) || !product.containsKey(CodeSmellFixer.LowerCase.TYPE) || !product.containsKey(CodeSmellFixer.LowerCase.QUANTITY)
                    || !product.containsKey(CodeSmellFixer.CamelCase.IS_INCREASE)) {
                    CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            int id = Integer.parseInt(product.get(CodeSmellFixer.LowerCase.ID).toString());
            String type = product.get(CodeSmellFixer.LowerCase.TYPE).toString();
            int extraQuantity = Integer.parseInt(product.get(CodeSmellFixer.LowerCase.QUANTITY).toString());
            Boolean isIncrease = Boolean.valueOf(product.get(CodeSmellFixer.CamelCase.IS_INCREASE).toString());
            if (Boolean.TRUE.equals(isIncrease)) {
                cartProduct = cartDAO.increaseProductInCartForUser(Integer.parseInt(userId.toString()), id, type, extraQuantity);
            } else {
                cartProduct = cartDAO.decreaseProductInCartForUser(Integer.parseInt(userId.toString()), id, type, extraQuantity);
            }

            if (cartProduct != null)
                return new ResponseEntity<>(cartProduct, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("inventory/cart")
    public ResponseEntity<UICartProducts[]> addProductsInCart(@RequestHeader Object userId,
            @RequestBody HashMap<String, Object> product) {
        LOG.log(Level.INFO, "Post /inventory/cart {0}, Product = {1}", new Object[]{userId, product});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 2) || userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (!product.containsKey(CodeSmellFixer.LowerCase.ID) || !product.containsKey(CodeSmellFixer.LowerCase.TYPE)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            UICartProducts[] cartProducts = cartDAO.addProductsInCart(Integer.parseInt(userId.toString()),
                    Integer.parseInt(product.get(CodeSmellFixer.LowerCase.ID).toString()), product.get(CodeSmellFixer.LowerCase.TYPE).toString(),
                    product.containsKey(CodeSmellFixer.LowerCase.QUANTITY) ? Integer.parseInt(product.get(CodeSmellFixer.LowerCase.QUANTITY).toString()) : 1);
            if (cartProducts != null)
                return new ResponseEntity<>(cartProducts, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_CART)) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("inventory/cartsession")
    public ResponseEntity<UICartSessions[]> addSessionsInCart(@RequestHeader Object userId,
            @RequestBody HashMap<String, Object> session) {
        LOG.log(Level.INFO, "Post /inventory/cartsession {0}, session = {1}", new Object[]{userId, session});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 2) || userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (!session.containsKey(CodeSmellFixer.LowerCase.NAME) || !session.containsKey(CodeSmellFixer.LowerCase.TIME)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            UICartSessions[] cartSessions = cartDAO.addSessionsInCart(Integer.parseInt(userId.toString()),
                    Integer.parseInt(session.get(CodeSmellFixer.LowerCase.TIME).toString()), session.get(CodeSmellFixer.LowerCase.NAME).toString());
            if (cartSessions != null)
                return new ResponseEntity<>(cartSessions, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_CART)) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("inventory/cart")
    public ResponseEntity<UICartProducts[]> deleteProductsInCart(@RequestHeader Object userId,
            @RequestBody HashMap<String, Object> product) {
        LOG.log(Level.INFO, "DELETE /inventory/cart {0}", new Object[]{userId});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 2) || userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (!product.containsKey(CodeSmellFixer.LowerCase.ID) || !product.containsKey(CodeSmellFixer.LowerCase.TYPE)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            int id = Integer.parseInt(product.get(CodeSmellFixer.LowerCase.ID).toString());
            String type = product.get(CodeSmellFixer.LowerCase.TYPE).toString();
            UICartProducts[] cartProducts = cartDAO.removeProductsInCart(Integer.parseInt(userId.toString()), id, type);
            if (cartProducts != null)
                return new ResponseEntity<>(cartProducts, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("inventory/cartsession")
    public ResponseEntity<UICartSessions[]> deleteSessionsInCart(@RequestHeader Object userId,
            @RequestBody HashMap<String, Object> session) {
        LOG.log(Level.INFO, "DELETE /inventory/cartsession {0}", new Object[]{userId});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 2) || userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (!session.containsKey(CodeSmellFixer.LowerCase.NAME) || !session.containsKey(CodeSmellFixer.LowerCase.TIME)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            int time = Integer.parseInt(session.get(CodeSmellFixer.LowerCase.TIME).toString());
            String name = session.get(CodeSmellFixer.LowerCase.NAME).toString();
            UICartSessions[] cartSessions = cartDAO.removeSessionsInCart(Integer.parseInt(userId.toString()), time, name);
            if (cartSessions != null)
                return new ResponseEntity<>(cartSessions, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("inventory/checkout")
    public ResponseEntity<HashMap<String, Object>> checkoutCart(@RequestHeader Object userId) {
        LOG.log(Level.INFO, "POST /inventory/checkout {0}", new Object[]{userId});
        HashMap<String, Object> response = new HashMap<>();
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            response.put("user_id", userId);

            Boolean isCheckOutDone = cartDAO.checkoutCart(Integer.parseInt(userId.toString()));
            response.put("is_checkout_done", isCheckOutDone);

            if (isCheckOutDone != null) {
                return new ResponseEntity<>(response,
                        Boolean.TRUE.equals(isCheckOutDone) ? HttpStatus.OK : HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("user/authenticate")
    public ResponseEntity<HashMap<String, Object>> authenticateUser(@RequestBody HashMap<String, String> credentials) {
        LOG.log(Level.INFO, "POST /user/authenticate {0}", new Object[]{credentials});
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (!credentials.containsKey(CodeSmellFixer.LowerCase.USERNAME) || !credentials.containsKey(CodeSmellFixer.LowerCase.PASSWORD)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            String userName = credentials.get(CodeSmellFixer.LowerCase.USERNAME);
            String password = credentials.get(CodeSmellFixer.LowerCase.PASSWORD);
            Boolean isAdmin = userDAO.authenticateAdmin(userName, password);
            Boolean isCustomer = userDAO.authenticateCustomer(userName, password);
            Boolean isAuthor = userDAO.authenticateAuthor(userName, password);

            response.put(CodeSmellFixer.SnakeCase.IS_ADMIN, isAdmin);
            response.put(CodeSmellFixer.SnakeCase.IS_CUSTOMER, isCustomer);
            response.put(CodeSmellFixer.SnakeCase.IS_AUTHOR, isAuthor);
            UserDetails userDtls = userDAO.getUserData(userName, password);
            if (null == userDtls) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            response.put(CodeSmellFixer.SnakeCase.USER_DATA, userDtls);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    } 

    @PostMapping("user/register")
    public ResponseEntity<HashMap<String,Object>> registeruser(@RequestBody HashMap<String,String> credentials) {
        LOG.log(Level.INFO, "POST /user/register {0}", new Object[]{credentials});
        HashMap<String,Object> response = new HashMap<>();
        try {
            if (!credentials.containsKey(CodeSmellFixer.LowerCase.USERNAME) || !credentials.containsKey(CodeSmellFixer.LowerCase.PASSWORD)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            String userName = credentials.get(CodeSmellFixer.LowerCase.USERNAME);
            String password = credentials.get(CodeSmellFixer.LowerCase.PASSWORD);
            Boolean isRegistrationDone = userDAO.registerCustomer(userName, password);
            if(Boolean.TRUE.equals(isRegistrationDone)){
                response.put(CodeSmellFixer.LowerCase.REGISTRATION, CodeSmellFixer.LowerCase.SUCCESS);
                response.put(CodeSmellFixer.LowerCase.USERNAME, userName);
                response.put(CodeSmellFixer.LowerCase.ID, userDAO.getUserData(userName, password).getId());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception ex){
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if(null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND) ){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } 
    } 

    @GetMapping("user/author")
    public ResponseEntity<UserDetails> getAuthorDetails(@RequestHeader Object userId, @RequestHeader Object authorId) {
        LOG.log(Level.INFO, "GET /user/author {0}, author_id = {1}", new Object[]{userId, authorId});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 3);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            UserDetails authorDetails = userDAO.getUserData(Integer.parseInt(authorId.toString()));
            return (null == authorDetails) ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(authorDetails, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping("inventory/product/images/{type}/{id}")
    public ResponseEntity<Product> deleteProductImages(@PathVariable String type, @PathVariable int id,
            @RequestHeader Object userId, @RequestBody HashMap<String, Object> product) {
        LOG.log(Level.INFO, "DELETE /inventory/product/images/{0}/{1} {2}, product = {3}", new Object[]{type, id, userId, product});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 1);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (!productDAO.checkIfProductImageIsValidInGivenMap(product)) {
                CodeSmellFixer.ExceptionThrower.throwInvalidBody();
            }
            Product oldProduct = productDAO.getProduct(id, type);
            if (oldProduct == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<String> images = (ArrayList<String>) (product.get(CodeSmellFixer.LowerCase.IMAGES));
            Product.Image oldProductImages = productDAO.getImageBasedOnType(type, oldProduct);
            List<String> oldProductImagesSrc = oldProductImages.getImageSrc();
            List<String> toBeDeletedImg = new ArrayList<>();
            for (String imageName : images) {
                if (oldProductImagesSrc.contains(imageName)) {
                    oldProductImagesSrc.remove(imageName);
                    toBeDeletedImg.add(imageName);
                }
            }

            Boolean isProductImageTypeChanged = !productDAO.getImageBasedOnType(type, oldProduct).getType()
                    .equals(Image.imageType.LOCAL);

            if (oldProductImagesSrc.isEmpty() && Boolean.TRUE.equals(!isProductImageTypeChanged)) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
            }

            if (Boolean.FALSE.equals(isProductImageTypeChanged)) {
                oldProductImages.setImageSrc(oldProductImagesSrc);
                product = new HashMap<>();
                product.put(CodeSmellFixer.LowerCase.ID, id);
                product.put(CodeSmellFixer.LowerCase.TYPE, type);
                productDAO.updateProduct(product);
            }

            for (String imageName : toBeDeletedImg) {
                productDAO.deleteImage(imageName, type);
            }

            return new ResponseEntity<>(productDAO.getProduct(id, type), HttpStatus.OK);
        } catch(IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, CodeSmellFixer.UpperCase.INVALID_BODY);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
    }

    @PostMapping("inventory/product/images/{type}/{id}")
    public ResponseEntity<Product> addProductImage(@PathVariable String type, @PathVariable int id,
            @RequestHeader Object userId, @RequestParam("file") MultipartFile productImage) {
        LOG.log(Level.INFO, "POST /inventory/product/images/{0}/{1} {2}", new Object[]{type, id, userId});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 1);
            if (Boolean.FALSE.equals(isValidUser)) {
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String fileName = productDAO.getValidImageName(id, type);
            String originalFileName = productImage.getOriginalFilename();
            originalFileName = (originalFileName == null) ? ".png" : originalFileName.split("\\.")[1];
            productDAO.addImage(productImage.getInputStream(), id, type,
                    fileName + "." + originalFileName);

            List<String> newProductImagesSrc = new ArrayList<>();
            newProductImagesSrc.add(fileName + "." + originalFileName);
            Product.Image image = new Image(imageType.LOCAL, newProductImagesSrc);

            HashMap<String, Object> product = new HashMap<>();
            product.put(CodeSmellFixer.LowerCase.ID, id);
            product.put(CodeSmellFixer.LowerCase.TYPE, type);
            product.put(CodeSmellFixer.LowerCase.IMAGE, image);
            productDAO.updateProduct(product);

            return new ResponseEntity<>(productDAO.getProduct(id, type), HttpStatus.OK);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            if (null != ex.getMessage() && ex.getMessage().equalsIgnoreCase(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("inventory/history/purchase")
    public ResponseEntity<UIPurchaseHistory[]> getPurchaseHistory(@RequestHeader Object userId) {
        LOG.log(Level.INFO, "GET inventory/history/purchase {0}", new Object[]{userId});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 3);
            if(Boolean.FALSE.equals(isValidUser)){
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            UIPurchaseHistory[] history = historyDAO.getPurchaseHistory(Integer.parseInt(userId.toString()));
            if (history != null)
                return new ResponseEntity<>(history,HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception ex){
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

    @GetMapping("inventory/history/inventory")
    public ResponseEntity<UIInventoryHistory[]> getInventoryHistory(@RequestHeader Object userId) {
        LOG.log(Level.INFO, "GET inventory/history/inventory {0}", new Object[]{userId});
        try {
            Boolean isValidUser = userDAO.authenticateUser(userId, 1);
            if(Boolean.FALSE.equals(isValidUser)){
                LOG.log(Level.SEVERE, CodeSmellFixer.LoggerCase.USER_UN_AUTHORIZED, userId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            UIInventoryHistory[] history = historyDAO.getInventoryHistory(Integer.parseInt(userId.toString()));
            if (history != null)
                return new ResponseEntity<>(history,HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception ex){
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

}