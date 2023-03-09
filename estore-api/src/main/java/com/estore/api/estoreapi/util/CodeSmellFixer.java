package com.estore.api.estoreapi.util;

public class CodeSmellFixer {
    public class LoggerCase{
        public static final String USER_UN_AUTHORIZED = "User Unauthorized : {0}";
        public static final String METHODNAME = "methodName ::: {0}";
        public static final String EXCEPTION = "Exception ::: {0}";

        private LoggerCase(){
        
        }
    }
    public class UpperCase{
        public static final String TYPE_NOT_FOUND = "TYPE_NOT_FOUND";
        public static final String INVALID_BODY = "INVALID_BODY";
        public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
        public static final String PRODUCT_ALREADY_IN_LIVE = "PRODUCT_ALREADY_IN_LIVE";
        public static final String PRODUCT_ALREADY_IN_CART = "PRODUCT_ALREADY_IN_CART";
        public static final String SESSION_ALREADY_IN_CART = "SESSION_ALREADY_IN_CART";
        public static final String IMAGE_TYPE_MISMATCH = "IMAGE_TYPE_MISMATCH";
        public static final String EMPTY_IMAGE_PATH = "EMPTY_IMAGE_PATH";
        public static final String IMAGE_NOT_FOUND = "IMAGE_NOT_FOUND";

        private UpperCase(){
        
        }
    }

    public class LowerCase{
        public static final String IMAGE = "image";
        public static final String TYPE = "type";
        public static final String AUTHOR = "author";
        public static final String ADMIN = "admin";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRODUCTS = "products";
        public static final String STATUS = "status";
        public static final String DELETED = "deleted";
        public static final String QUANTITY = "quantity";
        public static final String TIME = "time";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String REGISTRATION = "registration";
        public static final String SUCCESS = "success";
        public static final String IMAGES = "images";
        public static final String SESSIONS = "sessions";
        public static final String LIVE = "live";
        public static final String GUEST = "guest";
        public static final String AUDIO = "audio";
        public static final String VIDEO = "video";
        public static final String BOOK = "book";
        public static final String GET = "get";
        public static final String S = "s";

        private LowerCase(){
        
        }
    }

    public class CamelCase{
        public static final String IMAGE_SRC = "imageSrc";
        public static final String CREATED_BY = "createdBy";
        public static final String IS_INCREASE = "isIncrease";

        private CamelCase(){
        
        }
    }

    public class SnakeCase{
        public static final String FROM_SRC = "from_src";
        public static final String REQUEST_TYPE = "request_type";
        public static final String ADD_REQUEST = "add_request";
        public static final String UPDATE_REQUEST = "update_request";
        public static final String APPROVE_PRODUCT_REQUEST = "approve_product_request";
        public static final String IS_ADMIN = "is_admin";
        public static final String IS_CUSTOMER = "is_customer";
        public static final String IS_AUTHOR = "is_author";
        public static final String USER_DATA = "user_data";
        public static final String NOT_LIVE = "not_live";
        

        private SnakeCase(){
        
        }
    }

    public class CapitalizationCase{
        public static final String PRODUCTS = "Products";       

        private CapitalizationCase(){
        
        }
    }

    public class SpecialCharacter{
        public static final String SLASH = "/"; 
        public static final String DOT = ".";       
        public static final String DOT_REGEX = "\\."; 

        private SpecialCharacter(){
        
        }
    }
    
    private CodeSmellFixer(){
        
    }

    public static class ExceptionThrower{
        public static void throwInvalidBody() throws IllegalArgumentException{
            throw new IllegalArgumentException();
        }

        private ExceptionThrower(){
        
        }
    }


}
