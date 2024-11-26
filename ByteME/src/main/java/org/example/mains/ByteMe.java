package org.example.mains;

import org.example.helperItems.FoodItem;
import org.example.helperItems.Menu;
import org.example.managers.CustomerSerializer;
import org.example.managers.OrderManager;

import java.util.*;

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
            System.out.println("3. Exit");
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
                            CustomerSerializer.deserialize(customer, customers);
//                            CustomerSerializer.deserialize(customer);
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
                    System.out.println("Password is incorrect.");
                    break;
                case 3:
                    exit = true;
                    System.out.println("\nExiting...");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
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
                    GuiApp.viewGUI();
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
                    admin.viewAllItems();
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
