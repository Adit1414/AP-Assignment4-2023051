package org.example.managers;

import org.example.helperItems.FoodItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MenuSerializer {

    private static String add(FoodItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        JSONObject jsonItem = new JSONObject();
        jsonItem.put("name", item.getName());
        jsonItem.put("price", item.getPrice());
        jsonItem.put("category", item.getCategory());
        jsonItem.put("available", item.isAvailable());
        jsonItem.put("rating", item.getRating());

        return jsonItem.toString();
    }

    public static void saveToFile(FoodItem item) {
        String json = add(item);
        try {
            // Read the existing content from the file (if it exists)
            String existingContent = "";
            Path path = Paths.get("ByteME/data/menu.json");
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
//            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    // Deserialize a FoodItem object by updating its attributes
    public static FoodItem deserialize(FoodItem foodItem) {
        try {
            Path path = Paths.get("ByteME/data/menu.json");
            if (!Files.exists(path)) {
//                System.out.println("Menu file not found.");
                return foodItem;
            }

            String content = new String(Files.readAllBytes(path));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                if (jsonItem.getString("name").equalsIgnoreCase(foodItem.getName())) {
                    // Update the FoodItem's attributes
                    foodItem.setPrice(jsonItem.getInt("price"));
                    foodItem.setCategory(jsonItem.getString("category"));
                    foodItem.setAvailable(jsonItem.getBoolean("available"));
                    foodItem.setRating(jsonItem.getFloat("rating"));
//                    System.out.println("Attributes updated for food item: " + foodItem.getName());
                    return foodItem;
                }
            }

            System.out.println("Food item not found in menu: " + foodItem.getName());
        } catch (IOException e) {
//            System.out.println("Error reading the menu file: " + e.getMessage());
        }
        return foodItem;
    }

    // Update JSON data for a FoodItem object
    public static void updateJsonData(FoodItem item) {
        try {
            Path path = Paths.get("ByteME/data/menu.json");
            if (!Files.exists(path)) {
//                System.out.println("Menu file not found.");
                return;
            }

            String content = new String(Files.readAllBytes(path));
            JSONArray jsonArray = new JSONArray(content);

            boolean itemFound = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                if (jsonItem.getString("name").equalsIgnoreCase(item.getName())) {
                    jsonItem.put("price", item.getPrice());
                    jsonItem.put("category", item.getCategory());
                    jsonItem.put("available", item.isAvailable());
                    jsonItem.put("rating", item.getRating());
                    itemFound = true;
                    break;
                }
            }

            if (!itemFound) {
                System.out.println("Food item not found in menu: " + item.getName());
                return;
            }

            Files.write(path, jsonArray.toString(4).getBytes());
//            System.out.println("JSON data updated for food item: " + item.getName());

        } catch (IOException e) {
//            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }
}
