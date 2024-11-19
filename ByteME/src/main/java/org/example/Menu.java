package org.example;

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
        else System.out.println("This item already exists in the menu.");
        MenuSerializer.saveToFile(item);
    }
    public boolean removeItem(String name){
        boolean itemFound = false;
        for(List<FoodItem> foodList : this.items.values()){
            for (int i = 0; i < foodList.size(); i++){
                FoodItem food = foodList.get(i);
                if (food.getName().equalsIgnoreCase(name)){
                    foodList.remove(i);
                    itemFound=true;
                    break;
                }
            }
            if (itemFound) {
                break;  // Stop searching once the item has been found and updated
            }
        }
        if (!itemFound) {
            System.out.println(name + " not found in the menu.");
        }
        else {
            System.out.println(name + " was removed.");
        }
        return itemFound;
    }

    public FoodItem searchItem(String name){
        for(List<FoodItem> foodList : this.items.values()){
            for (FoodItem food : foodList) {
                if (food.getName().equalsIgnoreCase(name)) {
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
