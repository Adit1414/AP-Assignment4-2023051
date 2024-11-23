package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderingOutOfStockTest {
    private Menu menu;
    private Cart cart;

    @BeforeEach
    void setUp() {
        menu = Menu.getInstance();
        cart = new Cart();
        // Add an item to the menu and mark it as out of stock
        FoodItem item = new FoodItem("IceCream", 35, "Snacks");
        item.setAvailable(false);
        menu.addItem(item);
    }

    @Test
    void testOrderOutOfStockItem() {
        FoodItem item = menu.searchItem("IceCream");
        Assertions.assertFalse(item.isAvailable(), "Item should be marked as out of stock.");

        // Attempt to add to the cart
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cart.addItem(new OrderItem(item,1)));
        assertEquals("Item is out of stock.", exception.getMessage());
    }
}