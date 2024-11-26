package org.example.helperItems;

import org.json.JSONObject;

public class OrderItem {
    private final FoodItem item;
    private int quantity;

    public OrderItem(FoodItem item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", item.getName());
        jsonObject.put("quantity", quantity);
        return jsonObject;
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
}
