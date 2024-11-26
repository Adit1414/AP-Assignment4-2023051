package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerSerializer {

    private static String add(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        JSONObject jsonItem = new JSONObject();
        jsonItem.put("name", customer.getName());
        jsonItem.put("email", customer.getEmail());
        jsonItem.put("password", customer.getPassword());
        jsonItem.put("isVIP", customer.getIsVIP());
        jsonItem.put("orders history", customer.getOrdersHistory());
        jsonItem.put("current orders", customer.getCurrentOrders());
        jsonItem.put("cart", customer.getCart());

        return jsonItem.toString();
    }

    public static void saveToFile(Customer item) {
        String json = add(item);
        try {
            // Read the existing content from the file (if it exists)
            String existingContent = "";
            Path path = Paths.get("ByteME/data/customer.json");
            if (Files.exists(path)) {
                existingContent = new String(Files.readAllBytes(path));
            }

            // Parse the existing content as a JSONArray, or create a new one if empty
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Convert the new item to a JSONObject and append it to the array
            JSONObject jsonItem = new JSONObject(json);
            jsonArray.put(jsonItem);

            // Write the updated array back to the file
            Files.write(path, jsonArray.toString(4).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }


    public static void updateJsonData(Customer customer) {
        try {
            // Read the existing content from the file
            String existingContent = "";
            Path path = Paths.get("ByteME/data/customer.json");
            if (Files.exists(path)) {
                existingContent = new String(Files.readAllBytes(path));
            }

            // Parse the existing content as a JSONArray
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Iterate through the JSONArray and find the order to update
            boolean itemFound = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingItem = jsonArray.getJSONObject(i);
                if (existingItem.getString("email").equalsIgnoreCase(customer.getEmail())) {
                    // Order found, update its status
                    existingItem.put("cart", customer.cart.getOrderList());
                    existingItem.put("isVIP", customer.getIsVIP());

                    JSONArray currentOrdersArray = new JSONArray();
                    for (Order order : customer.getCurrentOrders()) {
                        currentOrdersArray.put(order.toJson());
                    }
                    existingItem.put("current orders", currentOrdersArray);

                    JSONArray ordersHistoryArray = new JSONArray();
                    for (Order order : customer.getCurrentOrders()) {
                        ordersHistoryArray.put(order.toJson());
                    }
                    existingItem.put("orders history", ordersHistoryArray);

                    itemFound = true;
                    break;
                }
            }

            if (itemFound) {
                // Write the updated JSON array back to the file
                Files.write(path, jsonArray.toString(4).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println(customer.getName() + " data was updated");
            } else {
                System.out.println(customer.getName() + " not found.");
            }

        } catch (IOException e) {
            System.out.println("Error reading or writing to JSON file: " + e.getMessage());
        }
    }

    public static void addCustomerData(Customer customer, List<Customer> customers) {
        try {
            // Read the existing content from the file
            String existingContent = "";
            Path path = Paths.get("ByteME/data/customer.json");
            if (Files.exists(path)) {
                existingContent = new String(Files.readAllBytes(path));
            }

            // Parse the existing content as a JSONArray
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Check if the customer already exists in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingItem = jsonArray.getJSONObject(i);

                if (existingItem.getString("email").equals(customer.getEmail())) {
                    // Populate customer's data from the JSON object
                    customer.setOrdersHistory(jsonToOrderList(existingItem, "orders history", customers));
                    customer.setCurrentOrders(jsonToOrderList(existingItem, "current orders", customers));
                    customer.setVIP(existingItem.getBoolean("isVIP"));
                    customer.setCart(jsonToCart(existingItem.getJSONArray("cart")));

                    return; // Customer data populated successfully
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        }
    }

    private static List<Order> jsonToOrderList(JSONObject customerJson, String key, List<Customer> customers) {
        List<Order> orderList = new ArrayList<>();

        // Extract the orders array for the given key (e.g., "orders history" or "current orders")
        JSONArray ordersJsonArray = customerJson.optJSONArray(key);
        if (ordersJsonArray == null) {
            System.out.println("Warning: Missing '" + key + "' field for customer: " + customerJson.optString("email"));
            return orderList;
        }

        String customerEmail = customerJson.optString("email", null);
        if (customerEmail == null) {
            System.out.println("Error: Missing 'email' field in customer object.");
            return orderList;
        }

        // Find the Customer object by email
        Customer customer = null;
        for (Customer c : customers) {
            if (c.getEmail().equals(customerEmail)) {
                customer = c;
                break;
            }
        }
        if (customer == null) {
            System.out.println("Error: No customer found with email: " + customerEmail);
            return orderList;
        }

        // Parse the orders from the JSON array
        for (int i = 0; i < ordersJsonArray.length(); i++) {
            try {
                JSONObject orderJson = ordersJsonArray.getJSONObject(i);

                JSONArray orderItemsJsonArray = orderJson.optJSONArray("items");
                if (orderItemsJsonArray == null) {
                    System.out.println("Warning: Missing 'items' in order at index " + i);
                    continue;
                }
                List<OrderItem> orderItemList = jsonToOrderItemList(orderItemsJsonArray);

                String address = orderJson.optString("address", "Unknown Address");
                String orderId = orderJson.optString("orderId", UUID.randomUUID().toString().substring(0, 8));
                String status = orderJson.optString("status", "Pending");
                String specialRequest = orderJson.optString("specialRequest", "");

                Order order = new Order(customer, orderItemList, address);
                order.setOrderId(orderId);
                order.setStatus(status);
                order.setSpecialRequest(specialRequest);

                orderList.add(order);
            } catch (JSONException e) {
                System.out.println("Error processing order at index " + i + ": " + e.getMessage());
            }
        }

        return orderList;
    }

    private static List<OrderItem> jsonToCart(JSONArray jsonArray) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject orderItemJson = jsonArray.getJSONObject(i);

            if(orderItemJson!=null) {
                // Extract details of OrderItem
//                String foodName = orderItemJson.getString("item");

                JSONObject foodItemJson = orderItemJson.getJSONObject("item");
                String name = foodItemJson.getString("name");
                FoodItem fi = Menu.getInstance().searchItem(name);

                int quantity = orderItemJson.getInt("quantity");


                // Create a new OrderItem
                OrderItem orderItem = new OrderItem(fi, quantity);

                orderItemList.add(orderItem);
            }
        }
        return orderItemList;
    }

    private static List<OrderItem> jsonToOrderItemList(JSONArray jsonArray) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject orderItemJson = jsonArray.getJSONObject(i);

                // Extract 'itemName' directly
                String name = orderItemJson.optString("itemName", null);
                if (name == null) {
                    System.out.println("Error: Missing 'itemName' in order item at index " + i);
                    continue; // Skip this item
                }

                // Look up the FoodItem
                FoodItem fi = Menu.getInstance().searchItem(name);
                if (fi == null) {
                    System.out.println("Warning: Food item not found in menu for name: " + name);
                    continue; // Skip if the FoodItem is not in the menu
                }

                // Extract the quantity
                int quantity = orderItemJson.optInt("quantity", 0);
                if (quantity <= 0) {
                    System.out.println("Warning: Invalid quantity for item: " + name);
                    continue; // Skip items with invalid quantity
                }

                // Create and add the OrderItem
                OrderItem orderItem = new OrderItem(fi, quantity);
                orderItemList.add(orderItem);
            } catch (JSONException e) {
                System.out.println("Error processing order item at index " + i + ": " + e.getMessage());
            }
        }
        return orderItemList;
    }
}