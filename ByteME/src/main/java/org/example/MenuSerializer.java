package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
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

        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }
//    public static void updateJsonData(FoodItem item) {
//        try {
//            // Read the existing content from the file
//            String existingContent = "";
//            if (Files.exists(Paths.get("ByteME/data/menu.json"))) {
//                existingContent = new String(Files.readAllBytes(Paths.get("ByteME/data/menu.json")));
//            }
//
//            // Parse the existing content as a JSONArray
//            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);
//
//            // Iterate through the JSONArray and find the order to update
//            boolean itemFound = false;
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject existingItem = jsonArray.getJSONObject(i);
//                if (existingItem.getString("name").equalsIgnoreCase(item.getName())) {
//                    // Order found, update its status
//                    existingItem.put("available", item.isAvailable());
//                    existingItem.put("rating", item.getRating());
//
//                    itemFound = true;
//                    break;
//                }
//            }
//
//            if (itemFound) {
//                // Write the updated JSON array back to the file
//                Files.write(Paths.get("ByteME/data/menu.json"), jsonArray.toString(4).getBytes(),
//                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//                System.out.println(item.getName() + " data was updated");
//            } else {
//                System.out.println(item.getName() + " not found.");
//            }
//
//        } catch (IOException e) {
//            System.out.println("Error reading or writing to JSON file: " + e.getMessage());
//        }
//    }
//    public static void deserialize(FoodItem foodItem) {
//        try {
//            // Check if the file exists and read its content
//            if (!Files.exists(Paths.get("ByteME/data/menu.json"))) {
//                System.out.println("Menu file not found.");
//                return;
//            }
//
//            String content = new String(Files.readAllBytes(Paths.get("ByteME/data/menu.json")));
//            JSONArray jsonArray = new JSONArray(content);
//
//            // Search for the matching food item in the JSON array
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonItem = jsonArray.getJSONObject(i);
//
//                if (jsonItem.getString("name").equalsIgnoreCase(foodItem.getName())) {
//                    // Update the attributes of the foodItem
//                    foodItem.setPrice(jsonItem.getInt("price"));
//                    foodItem.setCategory(jsonItem.getString("category"));
//                    foodItem.setAvailable(jsonItem.getBoolean("available"));
//                    foodItem.setRating(jsonItem.getFloat("rating"));
//                    System.out.println("Attributes updated for food item: " + foodItem.getName());
//                    return; // Exit after updating
//                }
//            }
//
//            System.out.println("Food item not found in menu: " + foodItem.getName());
//
//        } catch (IOException e) {
//            System.out.println("Error reading the menu file: " + e.getMessage());
//        } catch (Exception e) {
//            System.out.println("Error processing the menu data: " + e.getMessage());
//        }
//    }

    // Deserialize a FoodItem object by updating its attributes
    public static FoodItem deserialize(FoodItem foodItem) {
        try {
            if (!Files.exists(Paths.get("ByteME/data/menu.json"))) {
                System.out.println("Menu file not found.");
                return foodItem;
            }

            String content = new String(Files.readAllBytes(Paths.get("ByteME/data/menu.json")));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                if (jsonItem.getString("name").equalsIgnoreCase(foodItem.getName())) {
                    // Update the FoodItem's attributes
                    foodItem.setPrice(jsonItem.getInt("price"));
                    foodItem.setCategory(jsonItem.getString("category"));
                    foodItem.setAvailable(jsonItem.getBoolean("available"));
                    foodItem.setRating(jsonItem.getFloat("rating"));
                    System.out.println("Attributes updated for food item: " + foodItem.getName());
                    return foodItem;
                }
            }

            System.out.println("Food item not found in menu: " + foodItem.getName());
        } catch (IOException e) {
            System.out.println("Error reading the menu file: " + e.getMessage());
        }
        return foodItem;
    }

    // Update JSON data for a FoodItem object
    public static void updateJsonData(FoodItem item) {
        try {
            if (!Files.exists(Paths.get("ByteME/data/menu.json"))) {
                System.out.println("Menu file not found.");
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get("ByteME/data/menu.json")));
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

            Files.write(Paths.get("ByteME/data/menu.json"), jsonArray.toString(4).getBytes());
            System.out.println("JSON data updated for food item: " + item.getName());

        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }
}
