package org.example.helperItems;

import org.example.managers.MenuSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Menu {
    private final TreeMap<String, List<FoodItem>> items; //<Category, List<FoodItem>>
    private static Menu instance = null;

    private Menu() {
        this.items=new TreeMap<>();
    }

    // Get the singleton instance
    public static Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void addItem(FoodItem item) {
        String category = item.getCategory();

        // Check if the category exists in the TreeMap
        if (!items.containsKey(category)) {
            // If category does not exist, create a new ArrayList and add the item
            items.put(category, new ArrayList<>());
        }
        if(!items.get(category).contains(item)){
            items.get(category).add(item);
        }

        // Check if the item already exists in the menu by reading the existing JSON file
        if (itemExistsInFile(item)) {
            System.out.println("This item already exists in the menu.");
        } else {
            // Save the item to the file
            MenuSerializer.saveToFile(item);
            System.out.println(item.getName() + " added to the menu.");
        }
    }

    private boolean itemExistsInFile(FoodItem item) {
        try {
            // Read the existing content from the file
            String existingContent = "";

            Path path = Paths.get("ByteME/data/menu.json");
            if (Files.exists(path)) {
                existingContent = new String(Files.readAllBytes(path));
            }

            // Parse the existing content as a JSONArray
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Check if the item already exists in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingItem = jsonArray.getJSONObject(i);

                // Compare the item's name and category (you can extend this comparison to other attributes if needed)
                if (existingItem.getString("name").equals(item.getName()) &&
                        existingItem.getString("category").equals(item.getCategory())) {
                    return true; // Item already exists
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        }
        return false; // Item does not exist
    }

    public boolean removeItem(String name) {
        try {
            // Read the existing content from the file
            String existingContent = "";
            Path path = Paths.get("ByteME/data/menu.json");
            if (Files.exists(path)) {
                existingContent = new String(Files.readAllBytes(path));
            }

            // Parse the existing content as a JSONArray
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Iterate through the JSONArray and find the item to remove
            boolean itemFound = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingItem = jsonArray.getJSONObject(i);
                if (existingItem.getString("name").equalsIgnoreCase(name)) {
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
                System.out.println(name + " was removed.");
            } else {
                System.out.println(name + " not found in the menu.");
            }

            return itemFound;
        } catch (IOException e) {
            System.out.println("Error reading or writing to JSON file: " + e.getMessage());
            return false;
        }
    }


    public FoodItem searchItem(String name){
        for(List<FoodItem> foodList : this.items.values()){
            for (FoodItem food : foodList) {
                if (food.getName().equalsIgnoreCase(name)) {
                    MenuSerializer.deserialize(food);
                    return food;
                }
            }
        }
        System.out.println("Item with name '" + name + "' not found in the menu.");
        return null;
    }

    public List<FoodItem> searchByKeyword(String keyword){
        List<FoodItem> foods = new ArrayList<>();
        for(List<FoodItem> foodList : this.items.values()){
            for (FoodItem food : foodList) {
                if (food.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    foods.add(food);
                }
            }
        }
        return foods;
    }

    public void filterByCategory(String category){
        for(String s : items.keySet()){
            if(category.equalsIgnoreCase(s)){
                category=s;
            }
        }
        List<FoodItem> categoryFood= items.get(category);
        for (FoodItem food : categoryFood){
            System.out.println(food);
        }
    }

    public void sortByPrice(boolean ascending) {
        // Collect all FoodItems into a single list
        List<FoodItem> allItems = new ArrayList<>();
        for (List<FoodItem> foodList : items.values()) {
            allItems.addAll(foodList);
        }

        // Sort the list by price in the specified order
        allItems.sort(Comparator.comparingDouble(FoodItem::getPrice));
        if (!ascending) {
            Collections.reverse(allItems); // Reverse for descending order
        }

        for(FoodItem item : allItems){
            System.out.println(item);
        }
    }

    public void viewAllItems(){
        if(items.values().isEmpty()){
            System.out.println("There are no items.");
            return;
        }
        for (List<FoodItem> categoryList : items.values()){
            for (FoodItem food : categoryList){
                System.out.println(food);
            }
        }
    }
}
