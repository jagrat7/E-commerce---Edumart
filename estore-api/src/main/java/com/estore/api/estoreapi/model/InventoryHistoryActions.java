package com.estore.api.estoreapi.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonIgnoreProperties(value = {"userId", "timeStamp", "fieldChanges"})
public class InventoryHistoryActions {
    public enum Type{
        ADDED,
        UPDATED,
        DELETED;


        private static Map<String, Type> typeMap = new HashMap<String, Type>();

        static {
            typeMap.put("added", ADDED);
            typeMap.put("updated", UPDATED);
            typeMap.put("deleted", DELETED);
        }

         @JsonCreator
         public static Type forValue(String value) {
             return typeMap.get(value.toLowerCase());
         }
     
         @JsonValue
         public String toValue() {
             for (Entry<String, Type> entry : typeMap.entrySet()) {
                 if (entry.getValue() == this)
                     return entry.getKey();
             }
             return null;
         }
    }
    public enum SubType{
        PRODUCTS,
        AUTHOR;

        private static Map<String, SubType> subTypeMap = new HashMap<String, SubType>();

        static {
            subTypeMap.put("products", PRODUCTS);
            subTypeMap.put("author", AUTHOR);
        }

         @JsonCreator
         public static SubType forValue(String value) {
             return subTypeMap.get(value.toLowerCase());
         }
     
         @JsonValue
         public String toValue() {
             for (Entry<String, SubType> entry : subTypeMap.entrySet()) {
                 if (entry.getValue() == this)
                     return entry.getKey();
             }
             return null;
         }
    }
    public enum ProductType{
        VIDEO,
        AUDIO,
        BOOK;
       
        private static Map<String, ProductType> productTypeMap = new HashMap<String, ProductType>();

        static {
            productTypeMap.put("video", VIDEO);
            productTypeMap.put("audio", AUDIO);
            productTypeMap.put("book", BOOK);
        }

         @JsonCreator
         public static ProductType forValue(String value) {
             return productTypeMap.get(value.toLowerCase());
         }
     
         @JsonValue
         public String toValue() {
             for (Entry<String, ProductType> entry : productTypeMap.entrySet()) {
                 if (entry.getValue() == this)
                     return entry.getKey();
             }
             return null;
         }
    }
    @JsonProperty("type") private Type type;
    @JsonProperty("sub_type") private SubType sub_type;
    @JsonProperty("product_type") private ProductType product_type;
    @JsonProperty("id") private int id;
    @JsonProperty("name") private String name;
    @JsonProperty("field_changes") private FieldChanges[] field_changes;

    public InventoryHistoryActions(
        @JsonProperty("type") Type type,
        @JsonProperty("sub_type") SubType sub_type,
        @JsonProperty("product_type") ProductType product_type,
        @JsonProperty("name") String name,
        @JsonProperty("id") int id,
        @JsonProperty("field_changes") FieldChanges[] field_changes
    ){
        this.type = type;
        this.sub_type = sub_type;
        this.product_type = product_type;
        this.id = id;
        this.name = name;
        this.field_changes = field_changes;
    }

    public Type getType() {
        return type;
    }

    public SubType getSub_type() {
        return sub_type;
    }

    public ProductType getProduct_type() {
        return product_type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FieldChanges[] getFieldChanges() {
        return field_changes;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setSub_type(SubType sub_type) {
        this.sub_type = sub_type;
    }

    public void setProduct_type(ProductType product_type) {
        this.product_type = product_type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setField_changes(FieldChanges[] field_changes) {
        this.field_changes = field_changes;
    }

    public static class FieldChanges{
        public String field;
        public Object old_value;
        public Object new_value;

        public void setField(String field) {
            this.field = field;
        }

        public void setOld_value(Object old_value) {
            this.old_value = old_value;
        }

        public void setNew_value(Object new_value) {
            this.new_value = new_value;
        }
    }
}
