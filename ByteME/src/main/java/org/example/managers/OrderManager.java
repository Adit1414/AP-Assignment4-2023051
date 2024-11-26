package org.example.managers;

import org.example.helperItems.FoodItem;
import org.example.helperItems.Order;
import org.example.helperItems.OrderItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class OrderManager {
    private final List<Order> pendingOrders;
    private final PriorityQueue<Order> waitingQueue;
    private final ArrayList<Order> completedOrders;

    private final Map<String, Integer> itemFrequency = new HashMap<>();

    public OrderManager(){
        pendingOrders = new ArrayList<>();
        completedOrders = new ArrayList<>();
        waitingQueue = new PriorityQueue<>(
                (order1, order2) -> Boolean.compare(order2.isVip(), order1.isVip())
        );
    }

    public List<Order> getPendingOrders() {
        return pendingOrders;
    }

    // Adds a new order to the pending orders list, with VIP orders prioritized
    public void addOrder(Order order) {
       for(OrderItem orderItem : order.getOrderItemList()){
            FoodItem foodItem = MenuSerializer.deserialize(orderItem.getItem());
            if(!foodItem.isAvailable()){
                throw new IllegalArgumentException("Item is out of stock.");
            }
       }
        pendingOrders.add(order);
        System.out.println("Order added successfully.");
    }

    public void viewPendingOrders() {
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }
        System.out.println("VIP Orders: ");
        for (Order order : pendingOrders) {
            if(order.isVip()) {
                System.out.println(order);
            }
        }
        System.out.println("Regular Orders: ");
        for (Order order : pendingOrders) {
            if(!order.isVip()) {
                System.out.println(order);
            }
        }
    }

    // Updates the status of a specific order based on order ID
    public void updateOrderStatus(String orderId, String status) {
        for (Order order : pendingOrders) {
            if (order.getOrderId().equalsIgnoreCase(orderId)) {
                order.setStatus(status);
                System.out.println("Order status updated to: " + status);
                try {
                    // Read the existing content from the file
                    String existingContent = "";
                    Path path = Paths.get("ByteME/data/pendingOrders.json");
                    if (Files.exists(path)) {
                        existingContent = new String(Files.readAllBytes(path));
                    }

                    // Parse the existing content as a JSONArray
                    JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

                    // Iterate through the JSONArray and find the order to update
                    boolean itemFound = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject existingItem = jsonArray.getJSONObject(i);
                        if (existingItem.getString("orderId").equalsIgnoreCase(orderId)) {
                            // Order found, update its status
                            existingItem.put("status", status); // Use `put` to update the value
                            itemFound = true;
                            break;
                        }
                    }

                    if (itemFound) {
                        // Write the updated JSON array back to the file
                        Files.write(path, jsonArray.toString(4).getBytes(),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        System.out.println(orderId + " status was updated to: " + status);
                    } else {
                        System.out.println(orderId + " not found in pending orders.");
                    }

                } catch (IOException e) {
                    System.out.println("Error reading or writing to JSON file: " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("Order ID not found.");
    }

    // Updates the status of all pending orders containing a removed item
    public void updatePendingOrdersStatus(FoodItem item) {
        for (Order order : pendingOrders) {
            if(order.getFoodItemList().contains(item)){
                order.setStatus("Denied");
                order.getCustomer().getCurrentOrders().remove(order);
                order.getCustomer().getOrdersHistory().add(order);
                System.out.println("Updated status to Denied for orders with removed item: " + item.getName());
            }
        }
    }
    public void removeFromPendingOrders(FoodItem item) {
        Iterator<Order> iterator = pendingOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getFoodItemList().contains(item)) {
                iterator.remove();
                System.out.println("Order removed from pending orders");
            }
        }
    }

    // Generates a sales report with basic statistics
    public void generateSalesReport() {
        int totalSales = 0;
        int totalOrders = completedOrders.size() + pendingOrders.size();
        String mostPopularItem = calculateMostPopularItem();

        for (Order order : completedOrders) {
            totalSales += order.getTotalPrice();
        }
        for (Order order : pendingOrders) {
            totalSales += order.getTotalPrice();
        }

        System.out.println("---- Sales Report ----");
        System.out.println("Total Sales: " + totalSales);
        System.out.println("Total Orders: " + totalOrders);
        System.out.println("Most Popular Item: " + mostPopularItem);
        System.out.println("Frequency of all items: ");
        System.out.println(itemFrequency);
    }

    private String calculateMostPopularItem() {
        // Count the frequency of each item in completed orders
        for (Order order : completedOrders) {
            List<OrderItem> items = order.getOrderItemList();
            for (OrderItem item : items) {
                itemFrequency.put(item.getItem().getName(), itemFrequency.getOrDefault(item.getItem().getName(), 0) + item.getQuantity());
            }
        }
        for (Order order : pendingOrders) {
            List<OrderItem> items = order.getOrderItemList();
            for (OrderItem item : items) {
                itemFrequency.put(item.getItem().getName(), itemFrequency.getOrDefault(item.getItem().getName(), 0) + item.getQuantity());
            }
        }

        // Find the most popular item
        String mostPopularItem = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : itemFrequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopularItem = entry.getKey();
            }
        }
        return mostPopularItem;
    }

    public Order getOrderById(String orderId) {
        for (Order order : pendingOrders) {
            if (order.getOrderId().equalsIgnoreCase(orderId)) {
                return order;
            }
        }
        System.out.println("Order ID not found.");
        return null;
    }

    public void handleStatus(Order order, String status) {
        if (order == null) {
            return;
        }

        switch (status) {
            case "Accepted":
                // Add order to waitingQueue
                waitingQueue.add(order);
                System.out.println("\nOrder " + order.getOrderId() + " added to waiting queue.");

                // Assign "Preparing" status when the order is at the head of the queue
                prepareOrder(order);
                break;

            case "Denied":
                // Remove from pending orders
                pendingOrders.remove(order);
                System.out.println("\nOrder " + order.getOrderId() + " moved out of pending orders.");
                break;

            default:
                System.out.println("Invalid status or status not managed here.");
        }
    }

    private void prepareOrder(Order order) {
        Thread preparationThread = new Thread(() -> {
            try {
                synchronized (waitingQueue) {
                    // Wait until the order is at the head of the queue and no other orders are "Preparing"
                    while (waitingQueue.peek() != order ||
                            pendingOrders.stream().anyMatch(o -> o.getStatus().equalsIgnoreCase("Preparing"))) {
                        waitingQueue.wait(); // Wait for its turn
                    }
                }

                if(order!=null) {
                    order.setStatus("Preparing");
                    System.out.println("\nOrder " + order.getOrderId() + "  is now being prepared");

                    // Simulate preparation time
                    Thread.sleep(25000);
                    if(!order.getStatus().equalsIgnoreCase("Denied")) {
                        updateOrderStatus(order.getOrderId(), "Out for Delivery");
                        System.out.println("\nOrder " + order.getOrderId() + " is out for delivery.");

                        deliverOrder(order);
                    }
                }

            } catch (InterruptedException _) {
            }
        });
        preparationThread.start();
    }

    // Helper method to handle 'Out for Delivery' to 'Completed' transition
    private void deliverOrder(Order order) {
        Thread deliveryThread = new Thread(() -> {
            try {
                // Simulate delivery time
                Thread.sleep(15000);

                order.setStatus("Completed");
                System.out.println("\nOrder delivery completed for: " + order.getOrderId());

                // Move from pending to completed orders
                pendingOrders.remove(order);
                completedOrders.add(order);
                removeOrder(order.getOrderId());
                order.getCustomer().completeOrder(order);
                System.out.println("\nOrder " + order.getOrderId() + " moved to completed orders.");

                synchronized (waitingQueue) {
                    waitingQueue.remove(order); // Remove from waiting queue
                    waitingQueue.notifyAll(); // Notify waiting orders
                }

            } catch (InterruptedException _) {
            }
        });
        deliveryThread.start();
    }

    public void removeOrder(String orderId) {
        try {
            // Read the existing content from the file
            String existingContent = "";
            Path path = Paths.get("ByteME/data/pendingOrders.json");
            if (Files.exists(path)) {
                existingContent = new String(Files.readAllBytes(path));
            }

            // Parse the existing content as a JSONArray
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Iterate through the JSONArray and find the item to remove
            boolean itemFound = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingItem = jsonArray.getJSONObject(i);
                if (existingItem.getString("orderId").equalsIgnoreCase(orderId)) {
                    // Item found, remove it from the JSONArray
                    jsonArray.remove(i);
                    itemFound = true;
                    break;
                }
            }

            if (itemFound) {
                // Write the updated JSON array back to the file
                Files.write(path, jsonArray.toString(4).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println(orderId + " was removed.");
            } else {
                System.out.println(orderId + " not found in pending orders.");
            }

        } catch (IOException e) {
            System.out.println("Error reading or writing to JSON file: " + e.getMessage());
        }
    }
}
