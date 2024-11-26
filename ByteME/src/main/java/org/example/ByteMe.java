package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ByteMe {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Menu menu = Menu.getInstance();
    private static final List<Customer> customers = new ArrayList<>();
    private static final OrderManager orderManager = new OrderManager();
    private static Admin admin;
    public static void main(String[] args) {
        boolean exit = false;

        seedData();

        while (!exit)
        {
            System.out.println("\nWelcome to Byte Me!");
            System.out.println("1. Login as a customer");
            System.out.println("2. Login as an admin");
            System.out.println("3. Sign up as a customer");
            System.out.println("4. Exit");
            System.out.print("\nEnter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice)
            {
                case 1:
                    System.out.println("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter Password: ");
                    String pass = scanner.nextLine();
                    boolean found = false;
                    for(Customer customer : customers){
                        if(customer.getEmail().equals(email) && customer.getPassword().equals(pass)){
                            System.out.println("Welcome, " + customer.getName());
                            addCustomerData(customer);
                            found=true;
                            customerMenu(customer);
                            break;
                        }
                    }
                    if(!found) {
                        System.out.println("Email or password is incorrect.");
                    }
                    break;
                case 2:
                    System.out.println("Enter Password: ");
                    pass = scanner.nextLine();
                    if(admin.getPassword().equals(pass)){
                        System.out.println("Welcome, Admin");
                        adminMenu(admin);
                        break;
                    }

                    System.out.println("Email or password is incorrect.");
                    break;
                case 3:
                    signUp();
                    break;
                case 4:
                    exit = true;
                    System.out.println("\nExiting...");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
//
//    private static List<Order> jsonToOrderList(JSONArray jsonArray) {
//        List<Order> orderList = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject orderJson = jsonArray.getJSONObject(i);
//
//            // Extract the customer details (assuming customer is represented by email or other key)
//            String customerEmail = orderJson.getString("email");
//            Customer customer = null;
//
//            for(Customer c : customers){
//                if(c.getEmail().equals(customerEmail)){
//                    customer=c;
//                    break;
//                }
//            }
//
//            // Extract the order item list
//            JSONArray orderItemsJsonArray = orderJson.getJSONArray("orderItemList");
//            if(orderItemsJsonArray!=null) {
//                List<OrderItem> orderItemList = jsonToOrderItemList(orderItemsJsonArray);
//
//                // Extract the address
//                String address = orderJson.getString("address");
//
//                // Create a new Order object
//                Order order = new Order(customer, orderItemList, address);
//
//                // Set additional fields
//                order.setOrderId(orderJson.getString("orderId"));
//                order.setStatus(orderJson.getString("status"));
//                order.setSpecialRequest(orderJson.getString("specialRequest"));
//
//                // Optionally, if foodItemList needs to be handled separately, update it as needed
//                // order.updateFoodItemList() might already handle this
//
//                orderList.add(order);
//            }
//        }
//        return orderList;
//    }

//    private static List<Order> jsonToOrderList(JSONArray jsonArray) {
//        List<Order> orderList = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//                JSONObject orderJson = jsonArray.getJSONObject(i);
//
//                // Check if the required fields exist
//                if (!orderJson.has("email")) {
//                    System.out.println("Warning: Missing 'email' field in order object at index " + i);
//                    continue;
//                }
//
//                String customerEmail = orderJson.getString("email");
//
//                Customer customer = null;
//                for (Customer c : customers) {
//                    if (c.getEmail().equals(customerEmail)) {
//                        customer = c;
//                        break;
//                    }
//                }
//
//                if (customer == null) {
//                    System.out.println("Warning: No customer found with email: " + customerEmail);
//                    continue;
//                }
//
//                JSONArray orderItemsJsonArray = orderJson.optJSONArray("orderItemList");
//                if (orderItemsJsonArray != null) {
//                    List<OrderItem> orderItemList = jsonToOrderItemList(orderItemsJsonArray);
//
//                    String address = orderJson.optString("address", ""); // Default to empty string if missing
//
//                    Order order = new Order(customer, orderItemList, address);
//
//                    // Set additional fields
//                    order.setOrderId(orderJson.optString("orderId", "unknown"));
//                    order.setStatus(orderJson.optString("status", "Pending"));
//                    order.setSpecialRequest(orderJson.optString("specialRequest", ""));
//
//                    orderList.add(order);
//                } else {
//                    System.out.println("Warning: Missing or invalid 'orderItemList' field in order object at index " + i);
//                }
//            } catch (JSONException e) {
//                System.out.println("Error processing order at index " + i + ": " + e.getMessage());
//            }
//        }
//        return orderList;
//    }

    private static List<Order> jsonToOrderList(JSONObject customerJson, String key) {
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
                FoodItem fi = menu.searchItem(name);

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
                FoodItem fi = menu.searchItem(name);
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


//    private static void addCustomerData(Customer customer){
//        try {
//            // Read the existing content from the file
//            String existingContent = "";
//            if (Files.exists(Paths.get("ByteME/data/customer.json"))) {
//                existingContent = new String(Files.readAllBytes(Paths.get("ByteME/data/customer.json")));
//            }
//
//            // Parse the existing content as a JSONArray
//            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);
//
//            // Check if the item already exists in the JSON array
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject existingItem = jsonArray.getJSONObject(i);
//
//                // Compare the item's name and category (you can extend this comparison to other attributes if needed)
//                if (existingItem.getString("email").equals(customer.getEmail())) {
//                    customer.setOrdersHistory(jsonToOrderList(existingItem.getJSONArray("orders history")));
//                    customer.setCurrentOrders(jsonToOrderList(existingItem.getJSONArray("current orders")));
//                    customer.setVIP(existingItem.getBoolean("isVIP"));
//                    customer.setCart(jsonToOrderItemList(existingItem.getJSONArray("cart")));
//
//                    return; // Item already exists
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("Error reading the JSON file: " + e.getMessage());
//        }
//    }

    private static void addCustomerData(Customer customer) {
        try {
            // Read the existing content from the file
            String existingContent = "";
            if (Files.exists(Paths.get("ByteME/data/customer.json"))) {
                existingContent = new String(Files.readAllBytes(Paths.get("ByteME/data/customer.json")));
            }

            // Parse the existing content as a JSONArray
            JSONArray jsonArray = existingContent.isEmpty() ? new JSONArray() : new JSONArray(existingContent);

            // Check if the customer already exists in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject existingItem = jsonArray.getJSONObject(i);

                if (existingItem.getString("email").equals(customer.getEmail())) {
                    // Populate customer's data from the JSON object
                    customer.setOrdersHistory(jsonToOrderList(existingItem, "orders history"));
                    customer.setCurrentOrders(jsonToOrderList(existingItem, "current orders"));
                    customer.setVIP(existingItem.getBoolean("isVIP"));
                    customer.setCart(jsonToCart(existingItem.getJSONArray("cart")));

                    return; // Customer data populated successfully
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        }
    }


    private static void signUp(){
        //Sign up functionalities
    }

    public static void customerMenu(Customer customer) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\nCustomer Functionalities: ");
            System.out.println("1. Menu Operations");
            System.out.println("\t 1a. View all items");
            System.out.println("\t 1b. Search for an item");
            System.out.println("\t 1c. Filter by a category");
            System.out.println("\t 1d. Sort by price");
            System.out.println("2. Cart Operations");
            System.out.println("\t 2a. View cart");
            System.out.println("\t 2b. Add to cart");
            System.out.println("\t 2c. Remove from cart");
            System.out.println("\t 2d. View total");
            System.out.println("\t 2e. Checkout");
            System.out.println("3. Order Operations");
            System.out.println("\t 3a. View current orders' status");
            System.out.println("\t 3b. Cancel order");
            System.out.println("\t 3c. View order history");
            System.out.println("4. Review Operations");
            System.out.println("\t 4a. Add review");
            System.out.println("\t 4b. View reviews");
            System.out.println("5. VIP Operations");
            System.out.println("\t 5a. Why become a VIP?");
            System.out.println("\t 5b. Become a VIP");
            System.out.println("\t 5c. Check VIP Status");
            System.out.println("6. Logout\n");

            System.out.print("\nEnter your choice (e.g., 1a, 3b): ");
            String choice = scanner.nextLine().toLowerCase(Locale.ROOT);
            switch (choice) {
                //View all items
                case "1a":
                    customer.viewAllItems();
                    GuiApp.viewGUI(); // Prototype
                    break;
                //Search for an item
                case "1b":
                    System.out.println("Name of the item to be searched: ");
                    String name = scanner.nextLine();
                    customer.searchByKeyword(name);
                    break;
                //Filter by a category
                case "1c":
                    System.out.println("Name of the category: ");
                    String category = scanner.nextLine();
                    customer.filterByCategory(category);
                    break;
                //Sort by price
                case "1d":
                    customer.sortByPrice();
                    break;
                //View cart
                case "2a":
                    customer.viewCart();
                    break;
                //Add to cart
                case "2b":
                    customer.addToCart();
                    break;
                //Remove from cart
                case "2c":
                    customer.removeFromCart();
                    break;
                //View total
                case "2d":
                    customer.viewCartTotal();
                    break;
                //Checkout
                case "2e":
                    customer.checkout(orderManager);
                    break;
                //View current orders' status
                case "3a":
                    customer.viewCurrentOrdersStatus();
                    break;
                //Cancel order
                case "3b":
                    customer.cancelOrder(orderManager);
                    break;
                //View order history
                case "3c":
                    customer.viewOrderHistory(orderManager);
                    break;
                //Add a review
                case "4a":
                    customer.addReview();
                    break;
                //View reviews of an item
                case "4b":
                    customer.viewReviews();
                    break;
                //Why become a VIP
                case "5a":
                    customer.whyBecomeVIP();
                    break;
                //Become a VIP
                case "5b":
                    customer.createVIP();
                    break;
                //Check VIP status
                case "5c":
                    customer.checkVIPStatus();
                    break;
                //Logout
                case "6":
                    exit=true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }
    }

    public static void adminMenu(Admin admin){
        boolean exit = false;

        while (!exit) {
            System.out.println("\nAdmin Functionalities: ");
            System.out.println("1. Menu Operations");
            System.out.println("\t 1a. View all items");
            System.out.println("\t 1b. Add an item");
            System.out.println("\t 1c. Update an item");
            System.out.println("\t 1d. Remove an item");
            System.out.println("2. Order Operations");
            System.out.println("\t 2a. View pending orders");
            System.out.println("\t 2b. Update order status");
            System.out.println("3. Generate sales report");
            System.out.println("4. Logout\n");

            System.out.print("\nEnter your choice (e.g., 1a, 3b): ");
            String choice = scanner.nextLine().toLowerCase(Locale.ROOT);
            switch (choice) {
                case "1a":
//                    admin.viewAllItems();
                    GuiApp.viewGUI();
                    break;
                case "1b":
                    admin.addItem();
                    break;
                case "1c":
                    admin.updateItem();
                    break;
                case "1d":
                    admin.removeItem();
                    break;
                case "2a":
                    admin.viewPendingOrders();
                    break;
                case "2b":
                    admin.updateOrderStatus();
                    break;
                case "3":
                    admin.generateSalesReport();
                    break;
                case "4":
                    exit=true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }
    }

    public static void seedData(){
        admin = new Admin("ap", orderManager);

        Customer.add(new Customer("Customer", "c", "c"), customers);
        Customer.add(new Customer("Customer1", "c1@iiitd", "c1pass"), customers);
        Customer.add(new Customer("Customer2", "c2@iiitd", "c2pass"), customers);
        Customer.add(new Customer("Customer3", "c3@iiitd", "c3pass"), customers);

        menu.addItem(new FoodItem("Chowmein", 40, "Chinese"));
        menu.addItem(new FoodItem("Egg Chowmein", 50, "Chinese"));
        menu.addItem(new FoodItem("Chicken Chowmein", 70, "Chinese"));
        menu.addItem(new FoodItem("Veg Roll", 40, "Chinese"));
        menu.addItem(new FoodItem("Egg Roll", 50, "Chinese"));
        menu.addItem(new FoodItem("Chicken Roll", 70, "Chinese"));

        menu.addItem(new FoodItem("Alu Parantha", 30, "North Indian"));
        menu.addItem(new FoodItem("Alu Pyaz Parantha", 30, "North Indian"));
        menu.addItem(new FoodItem("Paneer Parantha", 40, "North Indian"));
        menu.addItem(new FoodItem("Mix Parantha", 40, "North Indian"));

        menu.addItem(new FoodItem("French Fries", 40, "Snacks"));
        menu.addItem(new FoodItem("Plain Maggi", 25, "Snacks"));
        menu.addItem(new FoodItem("Butter Maggi", 35, "Snacks"));
        menu.addItem(new FoodItem("Veg Maggi", 40, "Snacks"));
        menu.addItem(new FoodItem("Egg Maggi", 50, "Snacks"));

        menu.addItem((new FoodItem("Sting", 20, "Beverage")));
        menu.addItem((new FoodItem("Cold Coffee", 20, "Beverage")));
        menu.addItem((new FoodItem("Hot Coffee", 20, "Beverage")));
    }
}
