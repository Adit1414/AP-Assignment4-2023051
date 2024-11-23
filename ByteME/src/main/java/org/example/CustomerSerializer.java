package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
            if (Files.exists(Paths.get("ByteME/data/customer.json"))) {
                existingContent = new String(Files.readAllBytes(Paths.get("ByteME/data/customer.json")));
            }

            // Parse the existing content as a JSONArray, or create a new one if empty
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Convert the new item to a JSONObject and append it to the array
            JSONObject jsonItem = new JSONObject(json);
            jsonArray.put(jsonItem);

            // Write the updated array back to the file
            Files.write(Paths.get("ByteME/data/customer.json"), jsonArray.toString(4).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }


    public static void updateJsonData(Customer customer) {
        try {
            // Read the existing content from the file
            String existingContent = "";
            if (Files.exists(Paths.get("ByteME/data/customer.json"))) {
                existingContent = new String(Files.readAllBytes(Paths.get("ByteME/data/customer.json")));
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


//                    existingItem.put("current orders", this.currentOrders);
//                    existingItem.put("orders history", this.ordersHistory);

                    itemFound = true;
                    break;
                }
            }

            if (itemFound) {
                // Write the updated JSON array back to the file
                Files.write(Paths.get("ByteME/data/customer.json"), jsonArray.toString(4).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println(customer.getName() + " data was updated");
            } else {
                System.out.println(customer.getName() + " not found.");
            }

        } catch (IOException e) {
            System.out.println("Error reading or writing to JSON file: " + e.getMessage());
        }
    }
}