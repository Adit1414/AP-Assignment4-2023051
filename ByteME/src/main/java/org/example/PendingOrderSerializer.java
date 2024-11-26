package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PendingOrderSerializer {

    private static String add(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        JSONObject jsonItem = new JSONObject();
        jsonItem.put("orderId", order.getOrderId());
        jsonItem.put("order items", order.getOrderItemList());
        jsonItem.put("status", order.getStatus());
        jsonItem.put("special request", order.getSpecialRequest());
        jsonItem.put("total", order.getTotalPrice());
        jsonItem.put("address", order.getAddress());

        return jsonItem.toString();
    }

    public static void saveToFile(Order order) {
        String json = add(order);
        try {
            // Read the existing content from the file (if it exists)
            String existingContent = "";
            Path path = Paths.get("ByteME/data/pendingOrders.json");
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
}
