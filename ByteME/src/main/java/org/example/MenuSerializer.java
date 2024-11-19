package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MenuSerializer {

    public static String add(FoodItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        JSONObject jsonItem = new JSONObject();
        jsonItem.put("name", item.getName());
        jsonItem.put("price", item.getPrice());
        jsonItem.put("category", item.getCategory());
        jsonItem.put("available", item.isAvailable());

        return jsonItem.toString();
    }

    public static void saveToFile(FoodItem item) {
        String json = add(item);
        try {
            // Read the existing content from the file (if it exists)
            String existingContent = "";
            if (Files.exists(Paths.get("ByteME/data/menu.json"))) {
                existingContent = new String(Files.readAllBytes(Paths.get("ByteME/data/menu.json")));
            }

            // Parse the existing content as a JSONArray, or create a new one if empty
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Convert the new item to a JSONObject and append it to the array
            JSONObject jsonItem = new JSONObject(json);
            jsonArray.put(jsonItem);

            // Write the updated array back to the file
            Files.write(Paths.get("ByteME/data/menu.json"), jsonArray.toString(4).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Menu data saved successfully to " + "ByteME/data/menu.json");
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }
}
