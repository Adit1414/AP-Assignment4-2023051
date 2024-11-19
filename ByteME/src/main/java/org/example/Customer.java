package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Customer{
    private final String name;
    private final String email;
    private final String password;
    private boolean isVIP;
    private final List<Order> ordersHistory;
    private final List<Order> currentOrders;
    private static final Menu menu = Menu.getInstance();
    private final Cart cart;
    private final Scanner scanner = new Scanner(System.in);

    public Customer(String name, String email, String password){
        this.name=name;
        this.email=email;
        this.password=password;
        isVIP=false;
        ordersHistory = new ArrayList<>();
        currentOrders = new ArrayList<>();
        cart= new Cart();
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public List<Order> getOrdersHistory() {
        return ordersHistory;
    }

    public List<Order> getCurrentOrders() {
        return currentOrders;
    }

    //Menu Operations
    public void viewAllItems(){
        menu.viewAllItems();
    }
    public void searchByKeyword(String keyword){
        List<FoodItem> foods = menu.searchByKeyword(keyword);
        if(foods.isEmpty()){
            System.out.println("No such items in the menu");
            return;
        }
        System.out.println("Every item in the menu containing " + keyword + " in the name: ");
        for(FoodItem food : foods){
            System.out.println(food);
        }
    }
    public void filterByCategory(String category){
        menu.filterByCategory(category);
    }
    public void sortByPrice(){
        System.out.println("To sort in Ascending or Descending order (A/D): ");
        String ascending = scanner.nextLine();
        if (!ascending.equalsIgnoreCase("A") && !ascending.equalsIgnoreCase("D")){
            System.out.println("Enter A or D");
        }
        else {
            menu.sortByPrice(ascending.equalsIgnoreCase("A"));
        }
    }

    //Cart Operations
    public void viewCart(){
        cart.viewCart();
    }
    public void addToCart(){
        System.out.println("Name of item to add to cart: ");
        String name = scanner.nextLine();
        FoodItem item = menu.searchItem(name);
        if(item==null){
            return;
        }
        if(!item.isAvailable()){
            System.out.println("Item is not available right now. Try again later.");
            return;
        }

        OrderItem orderItem = cart.searchItem(name);

        if(orderItem==null) //If it's a new item being added to cart
        {
            System.out.println("Quantity of the item: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();
            orderItem = new OrderItem(item, quantity);
            cart.addItem(orderItem);
        }
        else //If the item is already in the cart, just adding the quantity
        {
            System.out.println("There are already " +orderItem.getQuantity() + " " + orderItem.getItem().getName() + " in the cart: ");
//            System.out.println(orderItem);
            System.out.println("Quantity to be added to this item in the cart: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();
            orderItem.setQuantity(orderItem.getQuantity()+quantity);
            cart.updateTotal();
            System.out.println(quantity + " " + orderItem.getItem().getName() + " added to cart.");
            System.out.println("Now there are " + orderItem.getQuantity() + " " + orderItem.getItem().getName() + " in cart.");
        }
    }
    public void removeFromCart(){
        System.out.println("Name of item to remove from cart: ");
        String name = scanner.nextLine();
        OrderItem orderItem = cart.searchItem(name);
        if(orderItem==null){
            return;
        }
        System.out.println(orderItem);
        System.out.println("Quantity of the item to remove: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        if (quantity>orderItem.getQuantity()){
            System.out.println("Invalid quantity.");
        }
        else if (quantity == orderItem.getQuantity()) {
            cart.getOrderList().remove(orderItem);
            cart.updateTotal();
            System.out.println(orderItem.getItem().getName() + " removed from cart.");
        }
        else {
            orderItem.setQuantity(orderItem.getQuantity()-quantity);
            cart.updateTotal();
            System.out.println(quantity + " items removed from cart.");
        }
    }
    public void viewCartTotal(){
        System.out.println("Total price: " + cart.getTotalPrice());
    }
    public void checkout(OrderManager orderManager){
        // Check if the cart is empty
        if (cart.getOrderList().isEmpty()) {
            System.out.println("Your cart is empty! Add items to your cart before checking out.");
            return;
        }
        for (OrderItem orderItem : cart.getOrderList()){
            if(!orderItem.getItem().isAvailable()){
                System.out.println(orderItem.getItem().getName() + " is not available right now, try again later.");
                return;
            }
        }
        for (OrderItem orderItem : cart.getOrderList()){
            if (menu.searchItem(orderItem.getItem().getName())==null){
                cart.getOrderList().remove(orderItem);
                cart.updateTotal();
                System.out.println(orderItem.getItem().getName() + " removed from cart.");
                return;
            }
        }
        // Display the total price
        int total = cart.getTotalPrice();
        System.out.println("Your total amount is: " + total);

        System.out.println("Enter special requests, if any: ");
        String request = scanner.nextLine();

        System.out.println("Enter Delivery address: ");
        String address = scanner.nextLine();
        // Confirm clear
        System.out.println("Do you want to proceed with the order? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {
            Order order = new Order(this, cart.getOrderList(), address);
            order.setSpecialRequest(request);
            orderManager.addOrder(order);
            currentOrders.add(order);
            this.cart.clear();
            System.out.println("Checkout successful! Thank you for your order.");
            if(order.isVip()){
                orderManager.handleStatus(order, "Accepted");
            }
        }
        else if(confirmation.equalsIgnoreCase("N")){
            System.out.println("Checkout cancelled. You can continue shopping.");
        }
        else {
            System.out.println("Enter Y or N. Checkout cancelled.");
        }
    }

    //Order Operations
    public void viewCurrentOrdersStatus(){
        if (currentOrders.isEmpty()) {
            System.out.println("You have no current orders.");
            return;
        }
        for (Order order : currentOrders) {
            System.out.println(order);
        }
    }
    public void cancelOrder(OrderManager orderManager){
        // Check if there are any current orders
        if (currentOrders.isEmpty()) {
            System.out.println("You have no current orders to cancel.");
            return;
        }

        // Display the list of current orders with an index for selection
        System.out.println("Current Orders:");
        for (Order order : currentOrders) {
            System.out.println(order);
        }

        // Ask the customer to select the order to cancel
        System.out.println("Enter the order ID you wish to cancel: ");
        String orderID = scanner.nextLine();

        boolean found = false;
        for(Order order : currentOrders){
            if(order.getOrderId().equalsIgnoreCase(orderID)){
                found = true;
                if(order.getStatus().equalsIgnoreCase("Received") || order.getStatus().equalsIgnoreCase("Accepted")) {
                    currentOrders.remove(order);
                    orderManager.getPendingOrders().remove(order);
                    order.setStatus("Cancelled");
                    System.out.println("Order canceled successfully: " + order.getOrderItemList());
                    ordersHistory.add(order);
                }
                else {
                    System.out.println("Cannot cancel order");
                }
                break;
            }
        }
        if(!found) {
            System.out.println("Incorrect OrderID");
        }
    }
    public void viewOrderHistory(OrderManager orderManager) {
        if (ordersHistory.isEmpty()) {
        System.out.println("You have no past orders.");
        return;
    }
        for (Order order : ordersHistory) {
            System.out.println(order);
        }

        System.out.println("Do you want to order anything from your past orders? (Y/N)");
        String confirmation = scanner.nextLine();

        if(confirmation.equalsIgnoreCase("Y")){
            boolean found = false;
            System.out.println("Enter order ID: ");
            String orderId = scanner.nextLine();
            for (Order order : ordersHistory) {
                if (order.getOrderId().equalsIgnoreCase(orderId)) {
                    found=true;
                    System.out.println("Items: " + order.getOrderItemList());
                    System.out.println("Total price: " + order.getTotalPrice());
                    orderFromHistory(orderManager, order.getOrderItemList());
                    break;
                }
            }
            if (!found) {
                System.out.println("Order ID not found.");
            }

        }
    }

    private void orderFromHistory(OrderManager orderManager, List<OrderItem> orderList){
        for (OrderItem orderItem : orderList){
            if(!orderItem.getItem().isAvailable()){
                System.out.println(orderItem.getItem().getName() + " is not available right now, try again later.");
                return;
            }
        }
        for (OrderItem orderItem : orderList){
            if (menu.searchItem(orderItem.getItem().getName())==null){
                return;
            }
        }
        System.out.println("Enter special requests, if any: ");
        String request = scanner.nextLine();

        System.out.println("Enter Delivery address: ");
        String address = scanner.nextLine();
        // Confirm clear
        System.out.println("Do you want to proceed with the order? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {
            Order order = new Order(this, orderList, address);
            order.setSpecialRequest(request);
            orderManager.addOrder(order);
            currentOrders.add(order);
            System.out.println("Checkout successful! Thank you for your order.");
        }
        else if(confirmation.equalsIgnoreCase("N")){
            System.out.println("Checkout cancelled. You can continue shopping.");
        }
        else {
            System.out.println("Enter Y or N. Checkout cancelled.");
        }
    }

    //Review Operations
    public void addReview(){
        System.out.println("Enter name of the food item to review: ");
        String name = scanner.nextLine();
        FoodItem item = menu.searchItem(name);
        if(item == null) {
            return;
        }
        System.out.println("Give rating out of 5: ");
        int rating = scanner.nextInt();
        scanner.nextLine();
        if (rating<1 || rating>5){
            System.out.println("Invalid rating.");
            return;
        }
        System.out.println("Give a comment: ");
        String comment = scanner.nextLine();
        Review review = new Review(rating, comment);
        item.addReview(review);
    }

    public void viewReviews() {
        System.out.println("Enter name of the food item to view reviews: ");
        String name = scanner.nextLine();
        FoodItem item = menu.searchItem(name);
        if(item == null) {
            return;
        }
        if(item.getReviews().isEmpty()){
            System.out.println("No reviews yet.");
            return;
        }
        System.out.println("Average rating: " + item.getRating());
        for(Review r : item.getReviews()){
            System.out.println(r);
        }
    }

    //VIP operations
    public void whyBecomeVIP(){
        System.out.println("A VIP order is always processed before other orders. " +
                "\nYou can become a VIP customer with just a one-time payment of 2000 rupees.");
    }
    public void createVIP(){
        if(isVIP){
            System.out.println("You are already a VIP.");
            return;
        }
        System.out.println("Price to become a VIP: 2000 rupees");
        System.out.println("Do you want to proceed with becoming a VIP? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {
            setVIP(true);
            System.out.println("Congratulations!! You are now a VIP customer of our canteen.");
        }
        else if(confirmation.equalsIgnoreCase("N")){
            System.out.println("Checkout cancelled.");
        }
        else {
            System.out.println("Enter Y or N. Checkout cancelled.");
        }
    }

    public void checkVIPStatus(){
        if(isVIP){
            System.out.println("You are a VIP Customer!");
        }
        else{
            System.out.println("You are not a VIP Customer yet.");
        }
    }

    public void completeOrder(Order order) {
        if(order!=null && currentOrders.contains(order)){
            currentOrders.remove(order);
            ordersHistory.add(order);
        }
    }
}
