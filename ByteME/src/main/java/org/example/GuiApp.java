package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;

public class GuiApp {
    public static void viewGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Swing Demo with Colors");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        // Create a CardLayout for switching between views
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Colors for styling
        Color headerColor = new Color(70, 130, 180);
        Color tableBackground = new Color(246, 135, 135);
        Color tableForeground = Color.BLACK; // Foreground is for text, Background is obv bg
        Color buttonBackground = new Color(6, 90, 241);
        Color buttonForeground = Color.WHITE;

        // First Panel: Menu Page
        JPanel menuPanel = new JPanel(new BorderLayout());
        JLabel menuLabel = new JLabel("Canteen Menu", SwingConstants.CENTER);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 24));
        menuLabel.setOpaque(true);
        menuLabel.setBackground(headerColor);
        menuLabel.setForeground(Color.WHITE);

        String[] menuColumns = {"Item Name", "Price", "Availability", "Category", "Rating"};
        String[][] menuData = loadMenuDataFromJson();
        JTable menuTable = new JTable(new DefaultTableModel(menuData, menuColumns));
        menuTable.setBackground(tableBackground);
        menuTable.setForeground(tableForeground);
        JScrollPane menuScrollPane = new JScrollPane(menuTable);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5)); // 2 rows, 1 column, 5px spacing

        JButton goToPendingOrdersButton = new JButton("View Pending Orders");
        goToPendingOrdersButton.setBackground(buttonBackground);
        goToPendingOrdersButton.setForeground(buttonForeground);

        JButton goToAllOrdersButton = new JButton("View All Orders");
        goToAllOrdersButton.setBackground(buttonBackground);
        goToAllOrdersButton.setForeground(buttonForeground);

        buttonPanel.add(goToPendingOrdersButton);
        buttonPanel.add(goToAllOrdersButton);

        menuPanel.add(menuLabel, BorderLayout.NORTH);
        menuPanel.add(menuScrollPane, BorderLayout.CENTER);
        menuPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Second Panel: Pending Orders Page
        JPanel pendingOrdersPanel = new JPanel(new BorderLayout());
        JLabel pendingOrdersLabel = new JLabel("Pending Orders", SwingConstants.CENTER);
        pendingOrdersLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pendingOrdersLabel.setOpaque(true);
        pendingOrdersLabel.setBackground(headerColor);
        pendingOrdersLabel.setForeground(Color.WHITE);

        String[] pendingOrdersColumns = {"Order ID", "Items", "Total", "Special Request", "Address", "Status"};
        String[][] pendingOrdersData = loadPendingOrderDataFromJson();

        JTable pendingOrdersTable = new JTable(new DefaultTableModel(pendingOrdersData, pendingOrdersColumns));
        pendingOrdersTable.setBackground(tableBackground);
        pendingOrdersTable.setForeground(tableForeground);
        JScrollPane pendingOrdersScrollPane = new JScrollPane(pendingOrdersTable);

        JButton backToMenuButton = new JButton("Back to Menu");
        backToMenuButton.setBackground(buttonBackground);
        backToMenuButton.setForeground(buttonForeground);

        pendingOrdersPanel.add(pendingOrdersLabel, BorderLayout.NORTH);
        pendingOrdersPanel.add(pendingOrdersScrollPane, BorderLayout.CENTER);
        pendingOrdersPanel.add(backToMenuButton, BorderLayout.SOUTH);

        // Third Panel: All Orders Page
        JPanel allOrdersPanel = new JPanel(new BorderLayout());
        JLabel allOrdersLabel = new JLabel("All Orders", SwingConstants.CENTER);
        allOrdersLabel.setFont(new Font("Arial", Font.BOLD, 24));
        allOrdersLabel.setOpaque(true);
        allOrdersLabel.setBackground(headerColor);
        allOrdersLabel.setForeground(Color.WHITE);

        String[] allOrdersColumns = {"Order ID", "Items", "Status"};
        String[][] allOrdersData = {
                {"1001", "Pizza x1", "Preparing"},
                {"1002", "Pasta x2", "Out for Delivery"},
                {"1000", "Burger x3", "Delivered"}
        };
        JTable allOrdersTable = new JTable(new DefaultTableModel(allOrdersData, allOrdersColumns));
        allOrdersTable.setBackground(tableBackground);
        allOrdersTable.setForeground(tableForeground);
        JScrollPane allOrdersScrollPane = new JScrollPane(allOrdersTable);

        JButton backToMenuButton1 = new JButton("Back to Menu");
        backToMenuButton1.setBackground(buttonBackground);
        backToMenuButton1.setForeground(buttonForeground);

        allOrdersPanel.add(allOrdersLabel, BorderLayout.NORTH);
        allOrdersPanel.add(allOrdersScrollPane, BorderLayout.CENTER);
        allOrdersPanel.add(backToMenuButton1, BorderLayout.SOUTH);


        // Add all panels to the main panel
        mainPanel.add(menuPanel, "MenuPage");
        mainPanel.add(pendingOrdersPanel, "PendingOrdersPage");
        mainPanel.add(allOrdersPanel, "AllOrdersPage");


        // Add Action Listeners for Buttons
        goToPendingOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "PendingOrdersPage");
            }
        });

        goToAllOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "AllOrdersPage");
            }
        });

        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MenuPage");
            }
        });

        backToMenuButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MenuPage");
            }
        });

        // Add main panel to the frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static String[][] loadMenuDataFromJson() {
        String[][] menuData = {};
        try {
            FileReader reader = new FileReader("ByteME/data/menu.json");
            StringBuilder stringBuilder = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                stringBuilder.append((char) i);
            }
            reader.close();

            // Parse the JSON data
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            menuData = new String[jsonArray.length()][5]; // 3 columns: Name, Price, Availability

            // Populate the menu data array
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject item = jsonArray.getJSONObject(j);
                menuData[j][0] = item.getString("name");
                menuData[j][1] = "₹" + item.getDouble("price"); // Assuming price is stored as a number
                menuData[j][2] = item.getBoolean("available") ? "Available" : "Out of Stock";
                menuData[j][3] = item.getString("category");
                menuData[j][4] = String.valueOf(item.getFloat("rating"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return menuData;
    }

    private static String[][] loadPendingOrderDataFromJson() {
        String[][] menuData = {};
        try {
            FileReader reader = new FileReader("ByteME/data/pendingOrders.json");
            StringBuilder stringBuilder = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                stringBuilder.append((char) i);
            }
            reader.close();

            // Parse the JSON data
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            menuData = new String[jsonArray.length()][6]; // 3 columns: Name, Price, Availability

            // Populate the menu data array
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject order = jsonArray.getJSONObject(j);

                // Format the "order items" as a string (could be order name and quantity)
                menuData[j][0] = order.getString("orderId");
                menuData[j][1] = extractOrderItems(order.getJSONArray("order items")); // Order Items Summary
                menuData[j][2] = "₹" + order.getDouble("total");
                menuData[j][3] = order.getString("special request");
                menuData[j][4] = order.getString("address");
                menuData[j][5] = order.getString("status");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return menuData;
    }

    private static String extractOrderItems(JSONArray orderItemsArray) {
        StringBuilder itemsSummary = new StringBuilder();
        for (int i = 0; i < orderItemsArray.length(); i++) {
            JSONObject orderItem = orderItemsArray.getJSONObject(i);
            JSONObject item = orderItem.getJSONObject("item");

            String itemName = item.getString("name");
            int quantity = orderItem.getInt("quantity");

            // Add item name and quantity to summary
            itemsSummary.append(itemName).append(" x").append(quantity);
            if (i < orderItemsArray.length() - 1) {
                itemsSummary.append(", "); // Separate items with a comma
            }
        }
        return itemsSummary.toString();
    }
}
