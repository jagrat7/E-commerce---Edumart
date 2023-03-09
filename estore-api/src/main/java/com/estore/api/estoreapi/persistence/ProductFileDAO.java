package com.estore.api.estoreapi.persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.estore.api.estoreapi.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.estore.api.estoreapi.model.Video;
import com.estore.api.estoreapi.model.Product.Image;
import com.estore.api.estoreapi.model.Product.Status;
import com.estore.api.estoreapi.model.Product.Image.imageType;
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.estore.api.estoreapi.model.Audio;
import com.estore.api.estoreapi.model.Book;

/**
 * Implements the functionality for JSON file-based peristance for Products
 *
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the instance into other classes as needed
 *
 * @author poorna chander (pp5109)
 */
@Component
public class ProductFileDAO implements ProductDAO {

    private static final Logger LOG = Logger.getLogger(ProductFileDAO.class.getName());
    private static final String PRODUCT_IMAGE_FOLDER = new File(System.getProperty("user.dir")).getParentFile().getPath() + "/estore-ui/" + "/src/assets/images/Products/";
    Map<Integer, Video> videos;
    Map<Integer, Audio> audios;
    Map<Integer, Book> books;
    private ObjectMapper objectMapper;
    private static int nextVideoID;
    private static int nextAudioID;
    private static int nextBookID;
    private List<String> allowedTypes;
    private String productsFileName;

    /**
     *
     * @param productsFileName file where all products are stored (for red/write)
     * @param videos_filename file where video products are specifically stored (for red/write)
     * @param books_filename file where book products are specifically stored (for red/write)
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * @throws IOException
     */
    public ProductFileDAO(@Value("${products.file}") String productsFileName,
            @Value("#{${allowed.types}}") List<String> allowedTypes,
            ObjectMapper objectMapper) throws IOException {
        this.productsFileName = productsFileName;
        this.allowedTypes = allowedTypes;
        this.objectMapper = objectMapper;
        initializeIds();
        load(); // load the heroes from the file
        saveProducts();
    }

    public static void initializeIds(){
        nextVideoID = 1;
        nextBookID = 1;
        nextAudioID = 1;
    }

    public static int generateNextVideoId(){
        return nextVideoID++;
    }

    public static int generateNextBookId(){
        return nextBookID++;
    }

    public static int generateNextAudioId(){
        return nextAudioID++;
    }

    public static int generateNextVideoId(int existingId){
        nextVideoID = existingId + 1;
        return nextVideoID;
    }

    public static int generateNextBookId(int existingId){
        nextBookID = existingId + 1;
        return nextBookID;
    }

    public static int generateNextAudioId(int existingId){
        nextAudioID = existingId + 1;
        return nextAudioID;
    }

     /**
     * Generates an array of {@linkplain Product products} from the tree map for all
     * {@linkplain Product products} 
     * <br> 
     * @return  The array of {@link Product products}, may be empty
     */
    private Product getAllProductsArray() {
        return getProductsArray(null, true, true, true);
    }

    private Product getProductsArray(String containsText, Boolean isIncludeDeletedData, Boolean isIncludeToBeAddedData, Boolean isIncludeLiveData) {
        return getProductsArray(containsText, isIncludeDeletedData, isIncludeToBeAddedData, isIncludeLiveData, null);
    }

   /**
     * Generates an array of {@linkplain Product products} from the tree map for any
     * {@linkplain Product products} that contains the text specified by containsText
     * <br>
     * If containsText is null, the array contains all of the {@linkplain Product products}
     * in the tree map
     * 
     * @return  The array of {@link Product products}, may be empty
     */
    private Product getProductsArray(String containsText, Boolean isIncludeDeletedData, Boolean isIncludeToBeAddedData, Boolean isIncludeLiveData, List<String> productTypes) {
        Product product = new Product();
        product.setAudios(productTypes == null || productTypes.contains(CodeSmellFixer.LowerCase.AUDIO) ? getAudiosArray(containsText, isIncludeDeletedData, isIncludeToBeAddedData, isIncludeLiveData) : null);
        product.setVideos(productTypes == null || productTypes.contains(CodeSmellFixer.LowerCase.VIDEO) ? getVideosArray(containsText, isIncludeDeletedData, isIncludeToBeAddedData, isIncludeLiveData) : null);
        product.setBooks(productTypes == null || productTypes.contains(CodeSmellFixer.LowerCase.BOOK) ? getBooksArray(containsText, isIncludeDeletedData, isIncludeToBeAddedData, isIncludeLiveData) : null);
        return product;
    }

    private Video[] getVideosArray(String containsText, Boolean isIncludeDeletedData, Boolean isIncludeToBeAddedData, Boolean isIncludeLiveData) {
        ArrayList<Video> videoArrayList = new ArrayList<>();


        for (Video video : videos.values()) {
            Boolean isNameMatches = ( containsText == null || (null != containsText && video.getName() != null &&  Pattern.compile(Pattern.quote(containsText), Pattern.CASE_INSENSITIVE).matcher(video.getName()).find()) );
            if(Boolean.TRUE.equals(isNameMatches) && ( 
                isIncludeDeletedData && video.isToBeDeleted() 
                || isIncludeLiveData && video.isLiveData()
                || isIncludeToBeAddedData && video.isToBeAdded()         
                                ) ){
                videoArrayList.add(video);
            }
        }

        Video[] videoArray = new Video[videoArrayList.size()];
        videoArrayList.toArray(videoArray);
        return videoArray;
    }

    private Audio[] getAudiosArray(String containsText, Boolean isIncludeDeletedData, Boolean isIncludeToBeAddedData, Boolean isIncludeLiveData) {
        ArrayList<Audio> audioArrayList = new ArrayList<>();

        for (Audio audio : audios.values()) {
            Boolean isNameMatches = ( containsText == null || (null != containsText && audio.getName() != null &&  Pattern.compile(Pattern.quote(containsText), Pattern.CASE_INSENSITIVE).matcher(audio.getName()).find()) );
            if(Boolean.TRUE.equals(isNameMatches) && ( 
                isIncludeDeletedData && audio.isToBeDeleted() 
                || isIncludeLiveData && audio.isLiveData()
                || isIncludeToBeAddedData && audio.isToBeAdded()         
                                ) ){
                audioArrayList.add(audio);
            }
        }

        Audio[] audioArray = new Audio[audioArrayList.size()];
        audioArrayList.toArray(audioArray);
        return audioArray;
    }

    private Book[] getBooksArray(String containsText, Boolean isIncludeDeletedData, Boolean isIncludeToBeAddedData, Boolean isIncludeLiveData) {
        ArrayList<Book> bookArrayList = new ArrayList<>();

        for (Book book : books.values()) {
            Boolean isNameMatches = ( containsText == null || (null != containsText && book.getName() != null &&  Pattern.compile(Pattern.quote(containsText), Pattern.CASE_INSENSITIVE).matcher(book.getName()).find()) );
            if(Boolean.TRUE.equals(isNameMatches) && ( 
                isIncludeDeletedData && book.isToBeDeleted() 
                || isIncludeLiveData && book.isLiveData()
                || isIncludeToBeAddedData && book.isToBeAdded()         
                                ) ){
                bookArrayList.add(book);
            }
        }

        Book[] bookArray = new Book[bookArrayList.size()];
        bookArrayList.toArray(bookArray);
        return bookArray;
    }
    /**
     * saves {@linkplain Product products} in-memory map of products as array of .json objects to a file specifiecd in application properties
     * @return true if save successful
     * @throws IOException
     */
    private void saveProducts() throws IOException {
        Product product= getAllProductsArray();
        objectMapper.writeValue(new File(productsFileName), product);
    }

    /**
     * Loads {@linkplain Product products} from the JSON file into the map
     *
     * Also sets next id (for videos and books together) to one more than the greatest id found in the file
     * @return true if file read was successful
     * @throws IOException
     */
    private void load() throws IOException {
        videos = new HashMap<>();
        books = new HashMap<>();
        audios = new HashMap<>();

        // Deserializes the JSON objects from the file into an array of Products
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        Product products = objectMapper.readValue(new File(productsFileName), Product.class);
        
        for(Video video : products.getVideos()){
            videos.put(video.getId(), video);
            generateNextVideoId(video.getId());
        }

        for(Audio audio : products.getAudios()){
            audios.put(audio.getId(), audio);
            generateNextAudioId(audio.getId());
        }

        for(Book book : products.getBooks()){
            books.put(book.getId(), book);
            generateNextBookId(book.getId());
        }

    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Product getProducts() throws IOException {
            return getProductsArray(null, false, false, true);
    }

     /**
     ** {@inheritDoc}
     */
    @Override
    public Product getProducts(Boolean isVideoDataNeeded, Boolean isAudioDataNeeded, Boolean isBookDataNeeded) throws IOException {
        List<String> productGroupsNeeded = new ArrayList<>();
        if(Boolean.TRUE.equals(isVideoDataNeeded)){
            productGroupsNeeded.add(CodeSmellFixer.LowerCase.VIDEO);
        }
        if(Boolean.TRUE.equals(isAudioDataNeeded)){
            productGroupsNeeded.add(CodeSmellFixer.LowerCase.AUDIO);
        }
        if(Boolean.TRUE.equals(isBookDataNeeded)){
            productGroupsNeeded.add(CodeSmellFixer.LowerCase.BOOK);
        }
        if(Boolean.TRUE.equals(!isVideoDataNeeded && !isAudioDataNeeded && !isBookDataNeeded)){
            productGroupsNeeded = null;
        }
        return getProductsArray(null, false, false, true, productGroupsNeeded);
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Product getToBeAddedProducts() throws IOException {
            return getProductsArray(null, false, true, false);
    }
    
    

    @Override
    public Product findProducts(String searchString) throws IOException {
            return getProductsArray(searchString, false, false, true);
    }

    @Override
    public Product getProduct(int id, String type) throws Exception {
        return getProduct(id, type, false);
    }

    public Product getProduct(int id, String type, Boolean includeDeleted) throws Exception {
        isGivenTypeAllowedExceptionCheck(type);
        Product product = null;
        if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO)){
            product = new Product();
            Video video = getVideo(id);
            if(null == video || (!includeDeleted && video.isToBeDeleted())){
                product = null;
            }else{
                product.setVideos(new Video[]{video});
            }
        }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO)){
            product = new Product();
            Audio audio = getAudio(id);
            if(null == audio || (!includeDeleted && audio.isToBeDeleted())){
                product = null;
            }else{
                product.setAudios(new Audio[]{audio});
            }
        }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK)){
            product = new Product();
            Book book = getBook(id);
            if(null == book || (!includeDeleted && book.isToBeDeleted())){
                product = null;
            }else{
                product.setBooks(new Book[]{book});
            }
        }
        
        return product;
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Video getVideo(int id) throws IOException {
            if (videos.containsKey(id))
                return videos.get(id);
            else
                return null;  
    }

    @Override
    public Audio getAudio(int id) throws IOException {
            if (audios.containsKey(id))
                return audios.get(id);
            else
                return null;  
    }

    @Override
    public Book getBook(int id) throws IOException {
            if (books.containsKey(id))
                return books.get(id);
            else
                return null;  
    }

    @Override
    public Product updateProduct(HashMap<String,Object> product) throws Exception {
            if ( !product.containsKey(CodeSmellFixer.LowerCase.TYPE) ){
                throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
            }
            isGivenTypeAllowedExceptionCheck(product.get(CodeSmellFixer.LowerCase.TYPE).toString());
            if ( !isGivenIdPresent( Integer.parseInt(product.get(CodeSmellFixer.LowerCase.ID).toString()), product.get(CodeSmellFixer.LowerCase.TYPE).toString() ) ){
                return null; // product does not exist
            }
            String type = product.get(CodeSmellFixer.LowerCase.TYPE).toString();
            Product updatedProduct = new Product();
            if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO)){
                updatedProduct.setVideos(new Video[]{createOrUpdateVideo(product, true)});
            }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO)){
                updatedProduct.setAudios(new Audio[]{createOrUpdateAudio(product, true)});
            }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK)){
                updatedProduct.setBooks(new Book[]{createOrUpdateBook(product, true)});
            }
            
            return updatedProduct;
    }

    private Video createOrUpdateVideo(HashMap<String,Object> video, Boolean isUpdate) throws Exception{
        Video oldVideoData = Boolean.TRUE.equals(isUpdate) ? videos.get(video.get(CodeSmellFixer.LowerCase.ID)) : new Video(nextVideoID);
        Video updatedVideo = null;
        try{
            Field[] productFields = removeUnwantedFields(Product.class.getDeclaredFields());
            Field[] videoFields = removeUnwantedFields(Video.class.getDeclaredFields());
            videoFields = concatProductAndChildArrays(productFields, videoFields);
            Object[] videoValues = new Object[videoFields.length];
            int index = 0;
            for(Field field : videoFields){
                if(video.containsKey(field.getName()) && !getSystemFields().contains(field.getName()))
                {
                    videoValues[index] = video.get(field.getName());
                }else{
                    boolean isFieldUpdatedSystematically = Boolean.FALSE;
                    if(getSystemFields().contains(field.getName())){
                        if(field.getName().equalsIgnoreCase(CodeSmellFixer.LowerCase.STATUS)){
                            if(video.containsKey(CodeSmellFixer.SnakeCase.REQUEST_TYPE)){

                                    if(video.containsKey(CodeSmellFixer.SnakeCase.FROM_SRC) 
                                    && video.get(CodeSmellFixer.SnakeCase.FROM_SRC).equals(CodeSmellFixer.LowerCase.AUTHOR) 
                                    &&  video.get(CodeSmellFixer.SnakeCase.REQUEST_TYPE).equals(CodeSmellFixer.SnakeCase.ADD_REQUEST) && Boolean.FALSE.equals(isUpdate)){
                                        videoValues[index] = Status.TO_BE_ADDED;
                                        isFieldUpdatedSystematically = Boolean.TRUE;
                                    }else if(video.get(CodeSmellFixer.SnakeCase.REQUEST_TYPE).equals(CodeSmellFixer.SnakeCase.UPDATE_REQUEST) && Boolean.TRUE.equals(isUpdate)){
                                        if(!oldVideoData.getStatus().equals(Status.TO_BE_ADDED)){
                                            throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE);
                                        }
                                        videoValues[index] = Status.IN_LIVE;
                                        isFieldUpdatedSystematically = Boolean.TRUE;
                                    }

                                }
                        }else if(field.getName().equalsIgnoreCase(CodeSmellFixer.CamelCase.CREATED_BY)){
                            if(Boolean.FALSE.equals(isUpdate)){
                                videoValues[index] = video.get(field.getName());
                                isFieldUpdatedSystematically = Boolean.TRUE;
                            }
                        }else if(field.getName().equalsIgnoreCase(CodeSmellFixer.LowerCase.IMAGE) && video.containsKey(field.getName())){
                            Image newImage = (Image)video.get(CodeSmellFixer.LowerCase.IMAGE);
                            if(newImage.getType().equals(Image.imageType.LOCAL)){
                                List<String> convertedImage = new ArrayList<>();
                                for(String imageName : newImage.getImageSrc()){
                                    if(convertedImage.contains(imageName)){
                                        continue;
                                    }
                                    if(!imageName.split("_")[0].equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO + oldVideoData.getId()))
                                    {
                                        convertedImage.add( copyImage(oldVideoData.getId(), CodeSmellFixer.LowerCase.VIDEO, imageName) );
                                    }else{
                                        convertedImage.add(imageName);
                                    }
                                }
                                newImage.setImageSrc(convertedImage);
                            }
                            if(Boolean.TRUE.equals(isUpdate)){
                            Object value = oldVideoData.getImage();
                            Image oldImage = ((Image)value);
                            if(oldImage == null){
                                oldImage = new Image(newImage.getType(), newImage.getImageSrc());
                            }
                            else if(oldImage.getType().equals(newImage.getType())){
                                if(oldImage.getType().equals(imageType.LOCAL)){
                                    List<String> oldImgSrc = oldImage.getImageSrc();
                                    oldImgSrc.addAll(newImage.getImageSrc());
                                    oldImage.setImageSrc(oldImgSrc);
                                }else{
                                    oldImage = newImage;
                                }
                            }else{
                                oldImage = newImage; 
                            }
                            videoValues[index] = oldImage;
                            isFieldUpdatedSystematically = Boolean.TRUE;
                            }else{
                            videoValues[index] = newImage;
                            isFieldUpdatedSystematically = Boolean.TRUE;
                            }
                        }
                    }
                    if(!isFieldUpdatedSystematically){
                        String fieldName = field.getName();
                        String methodName = CodeSmellFixer.LowerCase.GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        videoValues[index] = invokeMethodInProduct(oldVideoData, methodName);
                    }
                }
                index++;
            }

            int arrayIndex = 0;
            updatedVideo = new Video(Integer.parseInt(videoValues[arrayIndex++].toString()), checkStringNull(videoValues[arrayIndex++]), checkStringNull(videoValues[arrayIndex++]), Float.valueOf(videoValues[arrayIndex++].toString()), convertObjectToList(videoValues[arrayIndex++]), Float.valueOf(videoValues[arrayIndex++].toString()), convertObjectToList(videoValues[arrayIndex++]), checkStringNull(videoValues[arrayIndex++]), (Image)videoValues[arrayIndex++], (Status)videoValues[arrayIndex++], Integer.valueOf(videoValues[arrayIndex++].toString()), Long.valueOf(videoValues[arrayIndex++].toString()), convertObjectToList(videoValues[arrayIndex++]), convertObjectToList(videoValues[arrayIndex]));   
            videos.put(Boolean.TRUE.equals(isUpdate) ? Integer.parseInt("" + video.get(CodeSmellFixer.LowerCase.ID)) : nextVideoID, updatedVideo);
            saveProducts(); // may throw an IOException
            if(Boolean.FALSE.equals(isUpdate)){
                generateNextVideoId();
            }
         }catch(Exception ex){
            LOG.log(Level.INFO, CodeSmellFixer.LoggerCase.EXCEPTION, new Object[]{ex});
            if(ex.getMessage() != null && ex.getMessage().equals(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE)){
                throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE);
            }
         }
         return updatedVideo;
    }

    private Audio createOrUpdateAudio(HashMap<String,Object> audio, Boolean isUpdate) throws Exception{
        Audio oldAudioData = Boolean.TRUE.equals(isUpdate) ? audios.get(audio.get(CodeSmellFixer.LowerCase.ID)) : new Audio(nextAudioID);
        Audio updatedAudio = null;
        try{
            Field[] productFields = removeUnwantedFields(Product.class.getDeclaredFields());
            Field[] audioFields = removeUnwantedFields(Audio.class.getDeclaredFields());
            audioFields = concatProductAndChildArrays(productFields, audioFields);
            Object[] audioValues = new Object[audioFields.length];
            int index = 0;
            for(Field field : audioFields){
                if(audio.containsKey(field.getName()) && !getSystemFields().contains(field.getName()))
                {
                    audioValues[index] = audio.get(field.getName());
                }else{
                    boolean isFieldUpdatedSystematically = Boolean.FALSE;
                    if(getSystemFields().contains(field.getName())){
                        if(field.getName().equalsIgnoreCase(CodeSmellFixer.LowerCase.STATUS)){
                            if(audio.containsKey(CodeSmellFixer.SnakeCase.REQUEST_TYPE)){

                                    if(audio.containsKey(CodeSmellFixer.SnakeCase.FROM_SRC) 
                                    && audio.get(CodeSmellFixer.SnakeCase.FROM_SRC).equals(CodeSmellFixer.LowerCase.AUTHOR) 
                                    && audio.get(CodeSmellFixer.SnakeCase.REQUEST_TYPE).equals(CodeSmellFixer.SnakeCase.ADD_REQUEST) && Boolean.FALSE.equals(isUpdate)){
                                        audioValues[index] = Status.TO_BE_ADDED;
                                        isFieldUpdatedSystematically = Boolean.TRUE;
                                    }else if(audio.get(CodeSmellFixer.SnakeCase.REQUEST_TYPE).equals(CodeSmellFixer.SnakeCase.UPDATE_REQUEST) && Boolean.TRUE.equals(isUpdate)){
                                        if(!oldAudioData.getStatus().equals(Status.TO_BE_ADDED)){
                                            throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE);
                                        }
                                        audioValues[index] = Status.IN_LIVE;
                                        isFieldUpdatedSystematically = Boolean.TRUE;
                                    }

                                }
                        }else if(field.getName().equalsIgnoreCase(CodeSmellFixer.CamelCase.CREATED_BY)){
                            if(Boolean.FALSE.equals(isUpdate)){
                                audioValues[index] = audio.get(field.getName());
                                isFieldUpdatedSystematically = Boolean.TRUE;
                            }
                        }else if(field.getName().equalsIgnoreCase(CodeSmellFixer.LowerCase.IMAGE)){
                            Image newImage = (Image)audio.get(CodeSmellFixer.LowerCase.IMAGE);
                            if(newImage.getType().equals(Image.imageType.LOCAL)){
                                List<String> convertedImage = new ArrayList<>();
                                for(String imageName : newImage.getImageSrc()){
                                    if(!imageName.split("_")[0].equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO + oldAudioData.getId()))
                                    {
                                        convertedImage.add( copyImage(oldAudioData.getId(), CodeSmellFixer.LowerCase.AUDIO, imageName) );
                                    }else{
                                        convertedImage.add(imageName);
                                    }
                                }
                                newImage.setImageSrc(convertedImage);
                            }
                            if(Boolean.TRUE.equals(isUpdate)){
                            Object value = oldAudioData.getImage();
                            Image oldImage = ((Image)value);
                            if(oldImage == null){
                                oldImage = new Image(newImage.getType(), newImage.getImageSrc());
                            }
                            else if(oldImage.getType().equals(newImage.getType())){
                                if(oldImage.getType().equals(imageType.LOCAL)){
                                    List<String> oldImgSrc = oldImage.getImageSrc();
                                    oldImgSrc.addAll(newImage.getImageSrc());
                                    oldImage.setImageSrc(oldImgSrc);
                                }else{
                                    oldImage = newImage;
                                }
                            }else{
                                oldImage = newImage; 
                            }
                            audioValues[index] = oldImage;
                            isFieldUpdatedSystematically = Boolean.TRUE;
                        }else{
                            audioValues[index] = newImage;
                            isFieldUpdatedSystematically = Boolean.TRUE;
                        }
                    }
                    if(!isFieldUpdatedSystematically){
                        String fieldName = field.getName();
                        String methodName = CodeSmellFixer.LowerCase.GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        audioValues[index] = invokeMethodInProduct(oldAudioData, methodName);
                    }
                }
                index++;
            }

            int arrayIndex = 0;
            updatedAudio = new Audio(Integer.parseInt(audioValues[arrayIndex++].toString()), checkStringNull(audioValues[arrayIndex++]), checkStringNull(audioValues[arrayIndex++]), Float.valueOf(audioValues[arrayIndex++].toString()), convertObjectToList(audioValues[arrayIndex++]), Float.valueOf(audioValues[arrayIndex++].toString()), convertObjectToList(audioValues[arrayIndex++]), checkStringNull(audioValues[arrayIndex++]), (Image)audioValues[arrayIndex++], (Status)audioValues[arrayIndex++], Integer.parseInt(audioValues[arrayIndex++].toString()), Long.valueOf(audioValues[arrayIndex++].toString()), convertObjectToList(audioValues[arrayIndex]));   
            audios.put(Boolean.TRUE.equals(isUpdate) ? Integer.parseInt("" + audio.get(CodeSmellFixer.LowerCase.ID)) : nextAudioID, updatedAudio);
            saveProducts(); // may throw an IOException
            if(Boolean.FALSE.equals(isUpdate)){
                generateNextAudioId();
            }
         }
        }catch(Exception ex){
            LOG.log(Level.INFO, CodeSmellFixer.LoggerCase.EXCEPTION, new Object[]{ex});
            if(ex.getMessage() != null && ex.getMessage().equals(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE)){
                throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE);
            }
         }
         return updatedAudio;
    }

    private Book createOrUpdateBook(HashMap<String,Object> book, Boolean isUpdate) throws Exception{
        Book oldBookData = Boolean.TRUE.equals(isUpdate) ? books.get(book.get(CodeSmellFixer.LowerCase.ID)) : new Book(nextBookID);
        Book updatedBook = null;
        try{
            Field[] productFields = removeUnwantedFields(Product.class.getDeclaredFields());
            Field[] bookFields = removeUnwantedFields(Book.class.getDeclaredFields());
            bookFields = concatProductAndChildArrays(productFields, bookFields);
            Object[] bookValues = new Object[bookFields.length];
            int index = 0;
            for(Field field : bookFields){
                if(book.containsKey(field.getName()) && !getSystemFields().contains(field.getName()))
                {
                    bookValues[index] = book.get(field.getName());
                }else{
                    boolean isFieldUpdatedSystematically = Boolean.FALSE;
                    if(getSystemFields().contains(field.getName())){
                        if(field.getName().equalsIgnoreCase(CodeSmellFixer.LowerCase.STATUS)){
                            if(book.containsKey(CodeSmellFixer.SnakeCase.REQUEST_TYPE)){

                                    if(book.containsKey(CodeSmellFixer.SnakeCase.FROM_SRC) 
                                    && book.get(CodeSmellFixer.SnakeCase.FROM_SRC).equals(CodeSmellFixer.LowerCase.AUTHOR) 
                                    && book.get(CodeSmellFixer.SnakeCase.REQUEST_TYPE).equals(CodeSmellFixer.SnakeCase.ADD_REQUEST) && Boolean.FALSE.equals(isUpdate)){
                                        bookValues[index] = Status.TO_BE_ADDED;
                                        isFieldUpdatedSystematically = Boolean.TRUE;
                                    }else if(book.get(CodeSmellFixer.SnakeCase.REQUEST_TYPE).equals(CodeSmellFixer.SnakeCase.UPDATE_REQUEST) && Boolean.TRUE.equals(isUpdate)){
                                        if(!oldBookData.getStatus().equals(Status.TO_BE_ADDED)){
                                            throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE);
                                        }
                                        bookValues[index] = Status.IN_LIVE;
                                        isFieldUpdatedSystematically = Boolean.TRUE;
                                    }

                                }
                        }else if(field.getName().equalsIgnoreCase(CodeSmellFixer.CamelCase.CREATED_BY)){
                            if(Boolean.FALSE.equals(isUpdate)){
                                bookValues[index] = book.get(field.getName());
                                isFieldUpdatedSystematically = Boolean.TRUE;
                            }
                        }else if(field.getName().equalsIgnoreCase(CodeSmellFixer.LowerCase.IMAGE)){
                            Image newImage = (Image)book.get(CodeSmellFixer.LowerCase.IMAGE);
                            if(newImage.getType().equals(Image.imageType.LOCAL)){
                                List<String> convertedImage = new ArrayList<>();
                                for(String imageName : newImage.getImageSrc()){
                                    if(!imageName.split("_")[0].equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK + oldBookData.getId()))
                                    {
                                        convertedImage.add( copyImage(oldBookData.getId(), CodeSmellFixer.LowerCase.BOOK, imageName) );
                                    }else{
                                        convertedImage.add(imageName);
                                    }
                                }
                                newImage.setImageSrc(convertedImage);
                            }
                            Object value = oldBookData.getImage();
                            if(Boolean.TRUE.equals(isUpdate)){
                            Image oldImage = ((Image)value);
                            if(oldImage == null){
                                oldImage = new Image(newImage.getType(), newImage.getImageSrc());
                            }
                            else if(oldImage.getType().equals(newImage.getType())){
                                if(oldImage.getType().equals(imageType.LOCAL)){
                                    List<String> oldImgSrc = oldImage.getImageSrc();
                                    oldImgSrc.addAll(newImage.getImageSrc());
                                    oldImage.setImageSrc(oldImgSrc);
                                }else{
                                    oldImage = newImage;
                                }
                            }else{
                                oldImage = newImage; 
                            }
                            value = oldImage;
                            }else{
                                value = newImage;
                            }
                            bookValues[index] = value;
                            isFieldUpdatedSystematically = Boolean.TRUE;
                        }
                    }
                    if(!isFieldUpdatedSystematically){
                        String fieldName = field.getName();
                        String methodName = CodeSmellFixer.LowerCase.GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        bookValues[index] = invokeMethodInProduct(oldBookData, methodName);
                    }
                }
                index++;
            }

            int arrayIndex = 0;
            updatedBook = new Book(Integer.parseInt(bookValues[arrayIndex++].toString()), checkStringNull(bookValues[arrayIndex++]), checkStringNull(bookValues[arrayIndex++]), Float.valueOf(bookValues[arrayIndex++].toString()), convertObjectToList(bookValues[arrayIndex++]), Float.valueOf(bookValues[arrayIndex++].toString()), convertObjectToList(bookValues[arrayIndex++]), checkStringNull(bookValues[arrayIndex++]), (Image)bookValues[arrayIndex++], (Status)bookValues[arrayIndex++], Integer.parseInt(bookValues[arrayIndex++].toString()), checkStringNull(bookValues[arrayIndex++]), convertObjectToList(bookValues[arrayIndex++]), Integer.parseInt(bookValues[arrayIndex++].toString()), Float.parseFloat(bookValues[arrayIndex++].toString()), Integer.parseInt(bookValues[arrayIndex].toString()));   
            books.put(Boolean.TRUE.equals(isUpdate) ? Integer.parseInt("" + book.get(CodeSmellFixer.LowerCase.ID)) : nextBookID, updatedBook);
            saveProducts(); // may throw an IOException
            if(Boolean.FALSE.equals(isUpdate)){
                generateNextBookId();
            }
         }catch(Exception ex){
            LOG.log(Level.INFO, CodeSmellFixer.LoggerCase.EXCEPTION, new Object[]{ex});
            if(ex.getMessage() != null && ex.getMessage().equals(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE)){
                throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_ALREADY_IN_LIVE);
            }
         }
         return updatedBook;
    }

    private Object invokeMethodInProduct(Product product, String methodName){
        try {
            Method method = product.getClass().getMethod(methodName);
            return method.invoke(product);
        } catch (Exception ex) {
            LOG.log(Level.INFO, CodeSmellFixer.LoggerCase.METHODNAME, new Object[]{methodName});
            LOG.log(Level.INFO, CodeSmellFixer.LoggerCase.EXCEPTION, new Object[]{ex});
            return 0;
        }
    }

    private List<String> getSystemFields(){
        List<String> systemFields = new ArrayList<>();
        systemFields.add(CodeSmellFixer.LowerCase.STATUS);
        systemFields.add(CodeSmellFixer.LowerCase.IMAGE);
        systemFields.add(CodeSmellFixer.CamelCase.CREATED_BY);

        return systemFields;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> convertObjectToList(Object data){
        return (null == checkStringNull(data)) ? new ArrayList<>() : (ArrayList<T>)data;
    }

    public Field[] concatProductAndChildArrays(Field[] product, Field[] child) {
        Field[] result = Arrays.copyOf(product, product.length + child.length);
        System.arraycopy(child, 0, result, product.length, child.length);
        return result;
    }

    public Field[] removeUnwantedFields(Field[] fields){
        List<String> unwantedFields = Arrays.asList("audios","videos","books","LOG");
        List<Field> updatedFields = new ArrayList<>();
        for(Field field : fields){
            if(!unwantedFields.contains(field.getName())){
                updatedFields.add(field);
            }
        }
        Field[] updatedFieldsArray = new Field[updatedFields.size()];
        updatedFields.toArray(updatedFieldsArray);
        return updatedFieldsArray;
    }

    public boolean isGivenTypeAllowed(String type){
        return allowedTypes.contains(type);
    }

    public void isGivenTypeAllowedExceptionCheck(String type) throws Exception{
        if(!isGivenTypeAllowed(type)){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
    }

    public boolean isGivenIdPresent(int id, String type){
        boolean isPresent = isGivenTypeAllowed(type);
        isPresent = isPresent && (type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO) && ( videos.containsKey(id) && !videos.get(id).isToBeDeleted() ) ) || 
        (type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO) && ( audios.containsKey(id) && !audios.get(id).isToBeDeleted() ) ) ||
        (type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK) && ( books.containsKey(id) && !books.get(id).isToBeDeleted() ) );
        return isPresent;
    }

    public String checkStringNull(Object value){
        return (null == value) ? null : value.toString();
    }

    @Override
    public boolean deleteProduct(int id, String type) throws Exception {
            return deleteProduct(id, type, false);
    }

    public boolean deleteProduct(int id, String type, Boolean forceDelete) throws Exception {
            isGivenTypeAllowedExceptionCheck(type);
            if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO)){
                return deleteVideo(id, forceDelete);
            }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO)){
                return deleteAudio(id, forceDelete);
            }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK)){
                return deleteBook(id, forceDelete);
            }
            return false;
    }

    private boolean deleteVideo(int id, Boolean forceDelete) throws Exception {
            if(!videos.containsKey(id) || (Boolean.TRUE.equals(videos.get(id).isToBeDeleted()) && Boolean.TRUE.equals(!forceDelete))){
                return false;
            }

            if(Boolean.FALSE.equals(forceDelete)){
                Video video = videos.get(id);
                video.setStatus(Status.TO_BE_DELETED);
                videos.put(id, video);
            }else{
                videos.remove(id);
                deleteImageBasedOnId(CodeSmellFixer.LowerCase.VIDEO, id);
            }
 

            saveProducts();
            return true;
    }

    private boolean deleteAudio(int id, Boolean forceDelete) throws Exception {
            if(!audios.containsKey(id) || (Boolean.TRUE.equals(audios.get(id).isToBeDeleted()) && Boolean.TRUE.equals(!forceDelete))){
                return false;
            }

            if(Boolean.FALSE.equals(forceDelete)){
                Audio audio = audios.get(id);
                audio.setStatus(Status.TO_BE_DELETED);
                audios.put(id, audio);
            }else{
                audios.remove(id);
                deleteImageBasedOnId(CodeSmellFixer.LowerCase.AUDIO, id);
            }

            saveProducts();
            return true;
    }

    private boolean deleteBook(int id, Boolean forceDelete) throws Exception {
            if(!books.containsKey(id) || (Boolean.TRUE.equals(books.get(id).isToBeDeleted()) && Boolean.TRUE.equals(!forceDelete))){
                return false;
            }

            if(Boolean.FALSE.equals(forceDelete)){
                Book book = books.get(id);
                book.setStatus(Status.TO_BE_DELETED);
                books.put(id, book);
            }else{
                books.remove(id);
                deleteImageBasedOnId(CodeSmellFixer.LowerCase.BOOK, id);
            }

            saveProducts();
            return true;
    }

    /**
     * {@inheritDoc}
     * @throws Exception
     */
    @Override
    public Product createProduct(HashMap<String, Object> product ) throws Exception{
            if ( !product.containsKey(CodeSmellFixer.LowerCase.TYPE) ){
                throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
            }
            isGivenTypeAllowedExceptionCheck(product.get(CodeSmellFixer.LowerCase.TYPE).toString());
            String type = product.get(CodeSmellFixer.LowerCase.TYPE).toString();
            Product newProduct = null;
            if(product.containsKey(CodeSmellFixer.LowerCase.ID))
            {
                product.remove(CodeSmellFixer.LowerCase.ID);
            }
            if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO)){
                newProduct = new Product();
                newProduct.setVideos(new Video[]{createOrUpdateVideo(product, false)});
            }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO)){
                newProduct = new Product();
                newProduct.setAudios(new Audio[]{createOrUpdateAudio(product, false)});
            }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK)){
                newProduct = new Product();
                newProduct.setBooks(new Book[]{createOrUpdateBook(product, false)});
            }
            return newProduct;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean checkIfProductImageIsValidInGivenMap(HashMap<String,Object> product){
        if(!product.containsKey(CodeSmellFixer.LowerCase.IMAGES)){
            return false;
        }
        try{
            ArrayList<String> images = (ArrayList<String>)(product.get(CodeSmellFixer.LowerCase.IMAGES));
            if(images.isEmpty()){
                return false;
            }
        }catch(Exception ex){
            return false;
        }
        return true;
    }

    private String copyImage(int id, String type, String oldFileName) throws Exception{
            String productFolder = type.substring(0,1).toUpperCase() + type.substring(1, type.length()) + CodeSmellFixer.LowerCase.S;
            File fileDir = new File(PRODUCT_IMAGE_FOLDER + productFolder + CodeSmellFixer.SpecialCharacter.SLASH + oldFileName);
            String newFileName =  getValidImageName(id, type) + CodeSmellFixer.SpecialCharacter.DOT + oldFileName.split(CodeSmellFixer.SpecialCharacter.DOT_REGEX)[1];
            if(fileDir.exists()){
                addImage(Files.newInputStream(fileDir.toPath()), id, type, newFileName);
            }
            return newFileName;
    }

    @Override
    public void addImage(InputStream productImageBytes, int id, String type, String fileName) throws Exception{
        if ( !isGivenTypeAllowed( type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
        String productFolder = type.substring(0,1).toUpperCase() + type.substring(1, type.length()) + CodeSmellFixer.LowerCase.S;
        Path path = Paths.get(PRODUCT_IMAGE_FOLDER + productFolder + CodeSmellFixer.SpecialCharacter.SLASH + fileName);
        if(Files.exists(path)){
            fileName = getValidImageName(id, type);
            path = Paths.get(PRODUCT_IMAGE_FOLDER + productFolder + CodeSmellFixer.SpecialCharacter.SLASH + fileName);
        }
        Files.copy(productImageBytes, path);
        LOG.log(Level.INFO, "path - {0}", path);
        
    }

    @Override
    public String getValidImageName(int id, String type) throws Exception{
        if ( !isGivenTypeAllowed( type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
        Product product = getProduct(id, type);
        int fileNameSuffix = 0;
        if(product != null){
            Image image = getImageBasedOnType(type, product);
            if(image != null){
                List<String> imageSrc = image.getImageSrc();
                if(image.getType().equals(Image.imageType.LOCAL)){
                    for(String imageName : imageSrc){
                        int currentPrefix = Integer.parseInt(imageName.split("_")[1].split(CodeSmellFixer.SpecialCharacter.DOT_REGEX)[0]);
                        if(currentPrefix > fileNameSuffix){
                            fileNameSuffix = currentPrefix;
                        }
                    }
                }
            }
        }
        return type + id + "_" + (fileNameSuffix + 1);
    }

    @Override
    public void checkIfGivenProductImageIsValid(Image image, String type, int id) throws Exception{
        if ( !isGivenTypeAllowed( type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
        if ( id > 0 && !isGivenIdPresent( id, type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.PRODUCT_NOT_FOUND);
        }
        if( id > 0 ){
            Product oldProduct = getProduct(id, type);
            Image oldProductImage = getImageBasedOnType(type, oldProduct);
            if(!oldProductImage.getType().equals(image.getType()) && !oldProductImage.getImageSrc().isEmpty()){
                    throw new Exception(CodeSmellFixer.UpperCase.IMAGE_TYPE_MISMATCH);
            }
        }
        if(image.getType().equals(Image.imageType.LOCAL)){
            String productFolder = type.substring(0,1).toUpperCase() + type.substring(1, type.length()) + CodeSmellFixer.LowerCase.S;
            List<String> imageSrc = image.getImageSrc();
            if(imageSrc.isEmpty()){
                throw new Exception(CodeSmellFixer.UpperCase.EMPTY_IMAGE_PATH);
            }
            for(String imageName : imageSrc){
                File fileDir = new File(PRODUCT_IMAGE_FOLDER + productFolder + CodeSmellFixer.SpecialCharacter.SLASH + imageName);
                if(!fileDir.exists()){
                    throw new Exception(CodeSmellFixer.UpperCase.IMAGE_NOT_FOUND); 
                }
            }
        }else{
            if(image.getImageSrc().isEmpty()){
                throw new Exception(CodeSmellFixer.UpperCase.EMPTY_IMAGE_PATH);
            }
        }
    }

    public Image getImageBasedOnType(String type, Product product){
        if(type.equals(CodeSmellFixer.LowerCase.VIDEO)){
            return product.getVideos()[0].getImage();
        }else if(type.equals(CodeSmellFixer.LowerCase.AUDIO)){
            return product.getAudios()[0].getImage();
        }else if(type.equals(CodeSmellFixer.LowerCase.BOOK)){
            return product.getBooks()[0].getImage();
        }
        return null;
    }
    public Object getFieldBasedOnType(Product product, String type, String fieldName) throws Exception{
        String methodName = CodeSmellFixer.LowerCase.GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO)){
            Method method = product.getVideos()[0].getClass().getMethod(methodName);
            return  method.invoke(product.getVideos()[0]);
        }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO)){
            Method method = product.getAudios()[0].getClass().getMethod(methodName);
            return  method.invoke(product.getAudios()[0]);
        }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK)){
            Method method = product.getBooks()[0].getClass().getMethod(methodName);
            return  method.invoke(product.getBooks()[0]);
        }
        return null;
    }

    @Override
    public void deleteImageBasedOnId(String type, int id) throws Exception{
        if ( !isGivenTypeAllowed( type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
        String productFolder = type.substring(0,1).toUpperCase() + type.substring(1, type.length()) + CodeSmellFixer.LowerCase.S;
        File dir = new File(PRODUCT_IMAGE_FOLDER + productFolder);
        File[] files = dir.listFiles((d, name) -> name.startsWith(type + id + "_"));
        for(File file : files){
            deleteImage(file.getName(), type);
        }
        
    }

    @Override
    public void deleteImage(String fileName, String type) throws Exception{
        if ( !isGivenTypeAllowed( type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
        String productFolder = type.substring(0,1).toUpperCase() + type.substring(1, type.length()) + CodeSmellFixer.LowerCase.S;
        Path path = Paths.get(PRODUCT_IMAGE_FOLDER + productFolder + CodeSmellFixer.SpecialCharacter.SLASH + fileName);
        if(Files.exists(path)){
           Files.delete(path);
        }
        LOG.log(Level.INFO, "path - {0}", path);
    }

    @Override
    public void deleteExistingImages(int id, String type) throws Exception{
        if ( !isGivenTypeAllowed( type ) ){
            throw new Exception(CodeSmellFixer.UpperCase.TYPE_NOT_FOUND);
        }
        Product product = getProduct(id, type);
        Image existingImage = null;
        existingImage = getImageBasedOnType(type,product);

        if(existingImage.getType().equals(Image.imageType.LOCAL)){
            for(String imageName : existingImage.getImageSrc()){
                deleteImage(imageName, type);
            } 
        }
    }
    public HashMap<String,Object> convertProductToHashMapBasedOnType(Product product, String type){
        HashMap<String, Object> map = null;
        if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.VIDEO)){
            map = objectMapper.convertValue(product.getVideos()[0], new TypeReference<HashMap<String, Object>>() {});
        }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.AUDIO)){
            map = objectMapper.convertValue(product.getAudios()[0], new TypeReference<HashMap<String, Object>>() {});
        }else if(type.equalsIgnoreCase(CodeSmellFixer.LowerCase.BOOK)){
            map = objectMapper.convertValue(product.getBooks()[0], new TypeReference<HashMap<String, Object>>() {});
        }
        
        return map;
    }
}
