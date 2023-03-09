package com.estore.api.estoreapi.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.estore.api.estoreapi.model.Audio;
import com.estore.api.estoreapi.model.Book;
import com.estore.api.estoreapi.model.Product;
import com.estore.api.estoreapi.model.Video;
import com.estore.api.estoreapi.model.Product.Image;

/**
 * Defines the interface for Product object persistence
 *
 * @author poorna chander (pp5109)
 */
public interface ProductDAO {
    /**
     * Retrieves all {@linkplain Product products}
     *
     * @return list of products (Product fields)
     * @throws IOException
     */
    Product getProducts() throws IOException;
    Product getProducts(Boolean isVideoDataNeeded, Boolean isAudioDataNeeded, Boolean isBookDataNeeded) throws IOException;
    Product getToBeAddedProducts() throws IOException;


     /**
     * Retrieves a {@linkplain Product product} with the given id
     * 
     * @param id The id of the {@link Product product} to get
     * 
     * @return a {@link Product product} object with the matching id
     * <br>
     * null if no {@link Product product} with a matching id is found
     * 
     * @throws IOException if an issue with underlying storage
     * @throws Exception
     */
    Product getProduct(int id,String type) throws Exception;

    Video getVideo(int id) throws IOException;

    Book getBook(int id) throws IOException;

    Audio getAudio(int id) throws IOException;
    
    /**
     * Updates and saves a given a {@linkplain Product product}
     *
     * @param product object to be updated and saved
     * @return updated {@link Product product} if successful else null
     * @throws IOException if storage cannot be recognised
     * @throws Exception
     */
    Product updateProduct(HashMap<String,Object> product) throws Exception;

//    Book getBook(String id) throws IOException; //to be added by sprint 2
//    Video getVideo(String id) throws IOException; //to be added by sprint 2


    Product findProducts(String searchString) throws IOException;
    /**
     * Deletes a {@linkplain Book book} with the given id
     * 
     * @param id The id of the {@link Book book}
     * 
     * @return true if the {@link Book book} was deleted
     * <br>
     * false if book with the given id does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
//    boolean deleteBook(String id) throws IOException;

    /**
     * Deletes a {@linkplain Product product} with the given id
     * 
     * @param id The id of the {@link Product product}
     * 
     * @return true if the {@link Product product} was deleted
     * <br>
     * false if product with the given id does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     * @throws Exception
     */
    boolean deleteProduct(int id, String type) throws Exception;

    boolean deleteProduct(int id, String type, Boolean forceDelete) throws Exception;

    /**
     * Creates and saves a {@linkplain Product product}
     * @param product {@linkplain Product} object that needs to be created. New id will be created according to type
     * @return new {@linkplain Product} will be returned if successfully created
     * @throws IOException if there is an issue with underlying storage
     * @throws Exception
     */
    Product createProduct(HashMap<String, Object> product) throws Exception;


    boolean checkIfProductImageIsValidInGivenMap(HashMap<String, Object> product);

    void addImage(InputStream productImageBytes, int id, String type, String fileName) throws Exception;

    void deleteImageBasedOnId(String type, int id) throws Exception;

    void deleteImage(String fileName, String type) throws Exception;

    void deleteExistingImages(int id, String type) throws Exception;

    String getValidImageName(int id, String type) throws Exception;

    void checkIfGivenProductImageIsValid(Image image, String type, int id) throws Exception;

    Image getImageBasedOnType(String type, Product product); 
   
    /**
     * Deletes a {@linkplain Video video} with the given id
     * 
     * @param id The id of the {@link Video video}
     * 
     * @return true if the {@link Video video} was deleted
     * <br>
     * false if video with the given id does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
//    boolean deleteVideo(String id) throws IOException;

    Object getFieldBasedOnType(Product product, String type, String fieldName) throws Exception;

    HashMap<String,Object> convertProductToHashMapBasedOnType(Product product, String type);

}
