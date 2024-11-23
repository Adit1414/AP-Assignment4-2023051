import static org.junit.jupiter.api.Assertions.*;

import org.example.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class OrderingOutOfStockTest {
    private Menu menu;
    private Cart cart;
    private OrderManager orderManager = new OrderManager();
    private Customer customer = new Customer("Tester", "t", "t");

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
    void outOfStockTestCart() {
        FoodItem item = menu.searchItem("IceCream");
        Assertions.assertFalse(item.isAvailable(), "Item should be marked as out of stock.");

        // Attempt to add to the cart
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cart.addItem(new OrderItem(item,1)));
        assertEquals("Item is out of stock.", exception.getMessage());
    }
    @Test
    void outOfStockTestOrder() {
        FoodItem item = menu.searchItem("IceCream");
        Assertions.assertFalse(item.isAvailable(), "Item should be marked as out of stock.");

        List<OrderItem> orders = new ArrayList<>();
        orders.add(new OrderItem(item, 3));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderManager.addOrder(new Order(customer,orders,"")));
        assertEquals("Item is out of stock.", exception.getMessage());
    }
}