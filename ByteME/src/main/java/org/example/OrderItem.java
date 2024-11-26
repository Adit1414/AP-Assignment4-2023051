package org.example;

import org.json.JSONObject;

public class OrderItem {
    private FoodItem item;
    private int quantity;

    public OrderItem(FoodItem item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    public static OrderItem fromJson(JSONObject itemObject) {
        String name = itemObject.getString("itemName");
        FoodItem item = Menu.getInstance().searchItem(name);
        int quantity = itemObject.getInt("quantity");
        return new OrderItem(item, quantity);
    }
    public static OrderItem fromJsonCart(JSONObject itemObject) {
        JSONObject foodItemObject = itemObject.getJSONObject("item");
        String name = foodItemObject.getString("name");
        FoodItem item = Menu.getInstance().searchItem(name);
        int quantity = itemObject.getInt("quantity");
        return new OrderItem(item, quantity);
    }

    public int calcItemTotal(){
        return item.getPrice()*quantity;
    }

    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString(){
        return "Name: " + item.getName() + ", Quantity: " + String.valueOf(quantity);
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", item.getName());
        jsonObject.put("quantity", quantity);
        return jsonObject;
    }
}
