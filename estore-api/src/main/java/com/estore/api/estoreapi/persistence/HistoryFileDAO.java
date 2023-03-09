package com.estore.api.estoreapi.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.estore.api.estoreapi.model.History;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.estore.api.estoreapi.model.PurchaseHistory;
import com.estore.api.estoreapi.model.PurchaseHistoryProducts;
import com.estore.api.estoreapi.model.UICartProducts;
import com.estore.api.estoreapi.model.UICartSessions;
import com.estore.api.estoreapi.model.UIInventoryHistory;
import com.estore.api.estoreapi.model.UIPurchaseHistory;
import com.estore.api.estoreapi.model.InventoryHistoryActions.FieldChanges;
import com.estore.api.estoreapi.util.CodeSmellFixer;
import com.estore.api.estoreapi.model.InventoryHistory;
import com.estore.api.estoreapi.model.InventoryHistoryActions;
import com.estore.api.estoreapi.model.Product;

@Component
public class HistoryFileDAO implements HistoryDAO {
    private ObjectMapper objectMapper;
    private History historyDetails;
    HashMap<Long, PurchaseHistory> timeStampVsPurchaseHistory;
    HashMap<Long, InventoryHistory> timeStampVsInventoryHistory; 
    private String historyFileName;

    public HistoryFileDAO(@Value("${history.file}") String historyFileName, ObjectMapper objectMapper) throws Exception{
        this.objectMapper = objectMapper;
        this.historyFileName = historyFileName;
        historyDetails = objectMapper.readValue(new File(historyFileName), History.class);
        initializeData();
    }

    public void initializeData(){
        timeStampVsPurchaseHistory = new HashMap<>();
        timeStampVsInventoryHistory = new HashMap<>();

        for(PurchaseHistory purchase_history : historyDetails.getPurchase()){
            timeStampVsPurchaseHistory.put(purchase_history.getTimeStamp(), purchase_history);
        }

        for(InventoryHistory inventory_history : historyDetails.getInventory()){
            timeStampVsInventoryHistory.put(inventory_history.getTimeStamp(), inventory_history);
        }
    }

    public void saveHistory() throws Exception{
        List<PurchaseHistory> purchaseList = new ArrayList<>();
        List<InventoryHistory> inventoryList = new ArrayList<>();
        
        for(Long timestamp : timeStampVsPurchaseHistory.keySet()){
            purchaseList.add(timeStampVsPurchaseHistory.get(timestamp));
        }
        PurchaseHistory[] purchaseHistories = new PurchaseHistory[purchaseList.size()];
        purchaseList.toArray(purchaseHistories);
        
        for(Long timestamp : timeStampVsInventoryHistory.keySet()){
            inventoryList.add(timeStampVsInventoryHistory.get(timestamp));
        }
        InventoryHistory[] inventoryHistories = new InventoryHistory[inventoryList.size()];
        inventoryList.toArray(inventoryHistories);

        historyDetails.setInventory(inventoryHistories);
        historyDetails.setPurchase(purchaseHistories);
        objectMapper.writeValue(new File(historyFileName), historyDetails);
    }

    @Override
    public UIPurchaseHistory[] getPurchaseHistory(int userId) {
        List<UIPurchaseHistory> uiPurchaseList = new ArrayList<>();
        for(Long timestamp : timeStampVsPurchaseHistory.keySet()){
            if(timeStampVsPurchaseHistory.get(timestamp).getUserId() == userId){
                uiPurchaseList.add(new UIPurchaseHistory(timeStampVsPurchaseHistory.get(timestamp).getProducts(), timeStampVsPurchaseHistory.get(timestamp).getSessions(), timestamp));
            }
        }
        UIPurchaseHistory[] uiPurchase = new UIPurchaseHistory[uiPurchaseList.size()];
        uiPurchaseList.toArray(uiPurchase);
        return uiPurchase;
    }

    @Override
    public UIInventoryHistory[] getInventoryHistory(int userId) {
        List<UIInventoryHistory> uiInventoryList = new ArrayList<>();
        for(Long timestamp : timeStampVsInventoryHistory.keySet()){
            if(timeStampVsInventoryHistory.get(timestamp).getUserId() ==userId){
                uiInventoryList.add(new UIInventoryHistory(timeStampVsInventoryHistory.get(timestamp).getActions(), timestamp));
            }
        }
        UIInventoryHistory[] uiInventory = new UIInventoryHistory[uiInventoryList.size()];
        uiInventoryList.toArray(uiInventory);
        return uiInventory;
    }

    @Override
    public void addPurchaseHistory(int userId, long timestamp, PurchaseHistoryProducts[] products, UICartSessions[] sessions) throws Exception {
       PurchaseHistory purchaseData = new PurchaseHistory(userId, timestamp);
       purchaseData.setProducts(products);
       purchaseData.setSessions(sessions);
       timeStampVsPurchaseHistory.put(timestamp, purchaseData);
       saveHistory();
    }

    @Override
    public void addInventoryHistory(int userId, long timestamp, InventoryHistoryActions[] actions) throws Exception {
        InventoryHistory inventoryData = new InventoryHistory(userId, timestamp);
        inventoryData.setActions(actions);
        timeStampVsInventoryHistory.put(timestamp, inventoryData);
        saveHistory();
    }

    @Override
    public void addInventoryHistoryForProducts(int userId, long timeStamp, HashMap<String,Object> oldData, HashMap<String,Object> newData, InventoryHistoryActions.Type crudType) throws Exception{
        if(oldData == null){
            oldData = new HashMap<>();
        }
        InventoryHistoryActions inventoryHistoryActions = new InventoryHistoryActions(null, null, null, null, 0, new InventoryHistoryActions.FieldChanges[0]);
        inventoryHistoryActions.setType(crudType);
        inventoryHistoryActions.setId(Integer.parseInt(newData.get(CodeSmellFixer.LowerCase.ID).toString()));
        inventoryHistoryActions.setSub_type(InventoryHistoryActions.SubType.PRODUCTS);
        inventoryHistoryActions.setProduct_type(InventoryHistoryActions.ProductType.forValue(newData.get(CodeSmellFixer.LowerCase.TYPE).toString()));
        inventoryHistoryActions.setName(newData.get((CodeSmellFixer.LowerCase.NAME)).toString());
        newData.remove(CodeSmellFixer.LowerCase.ID);
        newData.remove(CodeSmellFixer.LowerCase.TYPE);
        if(crudType != InventoryHistoryActions.Type.UPDATED){
            newData.remove(CodeSmellFixer.LowerCase.NAME);
        }
        inventoryHistoryActions.setField_changes(getFieldChangesArray(newData, oldData));

        addInventoryHistory(userId, timeStamp, new InventoryHistoryActions[]{inventoryHistoryActions});
    }  

    @Override
    public void addInventoryHistoryForAuthor(int userId, long timeStamp, HashMap<String,Object> oldData, HashMap<String,Object> newData, InventoryHistoryActions.Type crudType) throws Exception{
        if(oldData == null){
            oldData = new HashMap<>();
        }
        InventoryHistoryActions inventoryHistoryActions = new InventoryHistoryActions(null, null, null, null, 0, new InventoryHistoryActions.FieldChanges[0]);
        inventoryHistoryActions.setType(crudType);
        inventoryHistoryActions.setId(Integer.parseInt(newData.get(CodeSmellFixer.LowerCase.ID).toString()));
        inventoryHistoryActions.setSub_type(InventoryHistoryActions.SubType.AUTHOR);
        inventoryHistoryActions.setProduct_type(null);
        inventoryHistoryActions.setName(newData.get((CodeSmellFixer.LowerCase.NAME)).toString());
        newData.remove(CodeSmellFixer.LowerCase.ID);
        newData.remove(CodeSmellFixer.LowerCase.NAME);
        HashMap<String,Object> modifiedData = new HashMap<>();
        List<String> allowedFields = allowedAuthorFields();
        for(String newDataKey : newData.keySet()){
            if(allowedFields.contains(newDataKey)){
                modifiedData.put(newDataKey, newData.get(newDataKey));
            }
        }
        inventoryHistoryActions.setField_changes(getFieldChangesArray(modifiedData, oldData));

        addInventoryHistory(userId, timeStamp, new InventoryHistoryActions[]{inventoryHistoryActions});
    }  

    private List<String> allowedAuthorFields(){
        List<String> allowedFields = new ArrayList<>();
        allowedFields.add("slots");

        return allowedFields;
    }

    public FieldChanges[] getFieldChangesArray(HashMap<String,Object> newData, HashMap<String,Object> oldData){
        List<FieldChanges> fieldChangeList = new ArrayList<>();
        for(String newDataKey : newData.keySet()){
            Object newDataValue = newData.get(newDataKey);
            Object oldDataValue = null;
            if(oldData.containsKey(newDataKey)){
                oldDataValue = oldData.get(newDataKey);
            }
            if(oldDataValue == null && newDataValue == null){
                continue;
            }
            if(oldDataValue != null){
                if(oldDataValue == newDataValue){
                    continue;
                }
                if(oldDataValue.equals(newDataValue)){
                    continue;
                }
                if(oldDataValue instanceof Float && newDataValue instanceof Integer && (Float.valueOf(oldDataValue.toString()) == (Float.parseFloat(newDataValue.toString())))){
                    continue;
                }
            }
           
            FieldChanges fieldChangeTemp = new FieldChanges();
            fieldChangeTemp.setField(newDataKey);
            fieldChangeTemp.setOld_value(oldDataValue);
            fieldChangeTemp.setNew_value(newDataValue);
            fieldChangeList.add(fieldChangeTemp);
        }
        FieldChanges[] fieldChangesArray = new FieldChanges[fieldChangeList.size()];
        fieldChangeList.toArray(fieldChangesArray);
        return fieldChangesArray;
    }

    @Override
    public void addPurchaseHistoryForCustomer(int userId, long timeStamp, UICartProducts[] cartProducts, UICartSessions[] sessions) throws Exception{
        List<PurchaseHistoryProducts> purchaseList = new ArrayList<>();
        for(UICartProducts cartProduct : cartProducts){
            Product productDtls = (Product)cartProduct.getProductDetails();
            purchaseList.add(new PurchaseHistoryProducts(cartProduct.getType(), productDtls.getId(), productDtls.getName(), cartProduct.getQuantity()));
        }

        PurchaseHistoryProducts[] purchaseHistoryProductsArray = new PurchaseHistoryProducts[purchaseList.size()];
        purchaseList.toArray(purchaseHistoryProductsArray);

        addPurchaseHistory(userId, timeStamp, purchaseHistoryProductsArray, sessions);
    }
}
