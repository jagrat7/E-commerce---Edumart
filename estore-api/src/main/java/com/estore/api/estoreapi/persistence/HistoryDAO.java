package com.estore.api.estoreapi.persistence;

import java.util.HashMap;

import com.estore.api.estoreapi.model.InventoryHistoryActions;
import com.estore.api.estoreapi.model.PurchaseHistoryProducts;
import com.estore.api.estoreapi.model.UICartProducts;
import com.estore.api.estoreapi.model.UICartSessions;
import com.estore.api.estoreapi.model.UIInventoryHistory;
import com.estore.api.estoreapi.model.UIPurchaseHistory;

public interface HistoryDAO {
    UIPurchaseHistory[] getPurchaseHistory(int userId);
    UIInventoryHistory[] getInventoryHistory(int userId);
    void addPurchaseHistory(int userId, long timestamp, PurchaseHistoryProducts[] products, UICartSessions[] sessions) throws Exception;
    void addInventoryHistory(int userId, long timestamp, InventoryHistoryActions[] actions) throws Exception;
    void addInventoryHistoryForProducts(int userId, long timeStamp, HashMap<String,Object> oldData, HashMap<String,Object> newData, InventoryHistoryActions.Type crudType) throws Exception;
    void addInventoryHistoryForAuthor(int userId, long timeStamp, HashMap<String,Object> oldData, HashMap<String,Object> newData, InventoryHistoryActions.Type crudType) throws Exception;
    void addPurchaseHistoryForCustomer(int userId, long timeStamp, UICartProducts[] cartProducts, UICartSessions[] sessions) throws Exception;
}
