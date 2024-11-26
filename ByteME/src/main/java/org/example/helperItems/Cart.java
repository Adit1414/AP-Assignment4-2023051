package org.example.helperItems;

import org.example.managers.MenuSerializer;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<OrderItem> orderList;
    private int totalPrice;

    public Cart(){
        orderList = new ArrayList<>();
        totalPrice = 0;
    }

    public List<OrderItem> getOrderList() {
        return orderList;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void viewCart() {
        if(orderList.isEmpty()){
            System.out.println("Cart is empty.");
            return;
        }
        for(OrderItem orderItem : orderList){
            System.out.println(orderItem + "\n");
        }
        System.out.println("Total Price= " + totalPrice);
    }

    public void addItem(OrderItem item){
        FoodItem foodItem = MenuSerializer.deserialize(item.getItem());
        if(!foodItem.isAvailable()){
            throw new IllegalArgumentException("Item is out of stock.");
        }
        else {
            orderList.add(item);
            updateTotal();
            System.out.println(item.getQuantity() + " " + item.getItem().getName() + " added to cart.");
        }
    }
    public OrderItem searchItem(String name){
        for (OrderItem orderItem : orderList){
            if (orderItem.getItem().getName().equalsIgnoreCase(name)){
                return orderItem;
            }
        }
        System.out.println(name + " not found in the cart.");
        return null;
    }

    public void updateTotal(){
        totalPrice=0;
        for(OrderItem orderItem : orderList){
            totalPrice+= orderItem.calcItemTotal();
        }
    }

    public void clear() {
        this.orderList= new ArrayList<>();
        totalPrice=0;
    }

    public void setOrdersList(List<OrderItem> cart) {
        this.orderList=cart;
        updateTotal();
    }

    public void updateItemQuantity(OrderItem orderItem, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        updateTotal();
        System.out.println(quantity + " " + orderItem.getItem().getName() + " added to cart.");
        System.out.println("Now there are " + orderItem.getQuantity() + " " + orderItem.getItem().getName() + " in cart.");
    }

    public int getItemQuantity(FoodItem item) {
        for(OrderItem orderItem : orderList){
            if(orderItem.getItem().equals(item)){
                return orderItem.getQuantity();
            }
        }
        return 0;
    }
}
