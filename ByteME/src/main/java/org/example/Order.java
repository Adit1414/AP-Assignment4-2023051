package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final Customer customer;
    private final List<OrderItem> orderItemList;
    private final List<FoodItem> foodItemList;
    private String orderId;
    private String status;
    private String specialRequest;
    private int totalPrice;
    private final String address;

    public Order(Customer customer, List<OrderItem> orderItemList, String address){
        this.customer=customer;
        this.orderItemList=orderItemList;
        this.status = "Received";
        calculateTotalPrice();
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.address = address;
        foodItemList = new ArrayList<>();
        updateFoodItemList();
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        jsonObject.put("status", status);

        JSONArray itemsArray = new JSONArray();
        for (OrderItem item : orderItemList) {
            itemsArray.put(item.toJson()); // Ensure OrderItem has a similar `toJson` method
        }
        jsonObject.put("items", itemsArray);

        return jsonObject;
    }

    private void updateFoodItemList() {
        for (OrderItem orderItem : orderItemList){
            foodItemList.add(orderItem.getItem());
        }
    }
    public List<FoodItem> getFoodItemList(){
        return foodItemList;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public String getOrderId(){
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    private void calculateTotalPrice(){
        for (OrderItem orderItem : orderItemList){
            totalPrice+=orderItem.calcItemTotal();
        }
    }

    public int getTotalPrice(){
        return totalPrice;
    }

    public boolean isVip() {
        return customer.getIsVIP();
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public String toString(){
        return "Items: " + orderItemList +
                "\nTotal Price: " + totalPrice +
                "\nStatus: " +  status +
                "\nSpecial Request: " + specialRequest +
                "\nOrder ID: " + orderId +
                "\nIs VIP: " + isVip() +
                "\nAddress: " + address +
                "\n";
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public String getAddress() {
        return address;
    }

    public void setOrderId(String orderId) {
        this.orderId=orderId;
    }
}
