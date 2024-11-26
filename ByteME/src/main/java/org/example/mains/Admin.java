package org.example.mains;

import org.example.helperItems.FoodItem;
import org.example.helperItems.Menu;
import org.example.helperItems.Order;
import org.example.managers.MenuSerializer;
import org.example.managers.OrderManager;

import java.util.Scanner;

public class Admin {
    private final String password;
    private final OrderManager orderManager;
    private static final Menu menu = Menu.getInstance();
    private final Scanner scanner = new Scanner(System.in);

    public Admin(String password, OrderManager orderManager) {
        this.password=password;
        this.orderManager=orderManager;
    }

    public String getPassword() {
        return password;
    }

    // Menu Operations
    public void viewAllItems() {
        menu.viewAllItems();
    }

    public void addItem() {
        System.out.println("Enter the item name: ");
        String name = scanner.nextLine();
        FoodItem item = menu.searchItem(name);
        if(item!=null){
            System.out.println("This item already exists in the menu.");
            System.out.println("Enter '1c' to update the item");
            return;
        }
        System.out.println("Enter the price: ");
        int price = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the category: ");
        String category = scanner.nextLine();
        item = new FoodItem(name, price, category);
        menu.addItem(item);
        System.out.println("Item added successfully!");
    }

    public void updateItem() {
        System.out.println("Enter name of item to update: ");
        String name = scanner.nextLine();
        FoodItem item = menu.searchItem(name);
        if (item == null) {
            return;
        }
        System.out.println(item);
        System.out.println("Enter new price or enter to skip: ");
        String price = scanner.nextLine();
        if (!price.isEmpty()) {
            item.setPrice(Integer.parseInt(price));
        }
        System.out.println("Update availability (true/false) or enter to skip: ");
        String availability = scanner.nextLine();
        if (!availability.isEmpty()) {
            item.setAvailable(Boolean.parseBoolean(availability));
        }
        if(availability.equals("false")){
            orderManager.updatePendingOrdersStatus(item);
            System.out.println("Pending orders updated.");
            orderManager.removeFromPendingOrders(item);
        }
        System.out.println("Enter new category or enter to skip: ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) {
            item.setCategory(category);
        }
        MenuSerializer.updateJsonData(item);
        System.out.println("Item updated successfully!");
    }

    public void removeItem() {
        System.out.println("Enter the name of the item to remove: ");
        String name = scanner.nextLine();
        FoodItem item = menu.searchItem(name);
        if (menu.removeItem(item.getName())) {
            orderManager.updatePendingOrdersStatus(item);
            orderManager.removeFromPendingOrders(item);
            System.out.println("Pending orders updated.");
        }
    }

    // Order Operations
    public void viewPendingOrders() {
        orderManager.viewPendingOrders();
    }

    public void updateOrderStatus() {
        System.out.println("Enter the order ID to update: ");
        String orderId = scanner.nextLine();
        Order order = orderManager.getOrderById(orderId);
        if(order==null){
            return;
        }
        if(order.getStatus().equals("Out For Delivery") || order.getStatus().equals("Completed")){
            System.out.println("Can't update status now, order is " + order.getStatus());
            return;
        }
        String acceptance;
        if(order.isVip()){
            System.out.println("The order is vip, hence it is accepted already");
            System.out.println("Do you want to deny the order? (Y/N)");
            acceptance = scanner.nextLine();
            if(acceptance.equalsIgnoreCase("Y")){
                orderManager.updateOrderStatus(orderId, "Denied");
                orderManager.handleStatus(order, "Denied");
            }
            else{
                orderManager.updateOrderStatus(orderId, "Accepted");
                orderManager.handleStatus(order, "Accepted");
            }
            return;
        }
        System.out.println("Deny or Accept order (0 or 1): ");
        acceptance = scanner.nextLine();
        if (!(acceptance.equals("0") || acceptance.equals("1"))) {
            System.out.println("Invalid input.");
            return;
        }
        String status;
        if(acceptance.equals("0")){
            status = "Denied";
        }
        else {
            status = "Accepted";
        }
        orderManager.updateOrderStatus(orderId, status);
        orderManager.handleStatus(order, status);
    }


    // Report Generation
    public void generateSalesReport() {
        orderManager.generateSalesReport();
    }
}
