package org.example;

public class OrderItem {
    private FoodItem item;
    private int quantity;

    public OrderItem(FoodItem item, int quantity){
        this.item = item;
        this.quantity = quantity;
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
