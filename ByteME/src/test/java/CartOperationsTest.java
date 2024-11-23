import static org.junit.jupiter.api.Assertions.*;

import org.example.Cart;
import org.example.FoodItem;
import org.example.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartOperationsTest {
    private Cart cart;
    private FoodItem item;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        item = new FoodItem("Samosa", 20, "Snacks");
    }

    @Test
    void testAddItemToCart() {
        OrderItem orderItem = new OrderItem(item, 2);
        cart.addItem(orderItem);
        assertEquals(2, cart.getItemQuantity(item), "Item quantity should be updated.");
        assertEquals(40, cart.getTotalPrice(), 0.01, "Total price should be calculated correctly.");
    }

    @Test
    void testUpdateItemQuantity() {
        orderItem = new OrderItem(item, 1);
        cart.addItem(orderItem);
        cart.updateItemQuantity(orderItem, 3);
        assertEquals(4, cart.getItemQuantity(item), "Item quantity should be updated.");
        assertEquals(80, cart.getTotalPrice(), 0.01, "Total price should be updated.");
    }

    @Test
    void testInvalidItemQuantity() {
        // Initialize and add orderItem to the cart
        orderItem = new OrderItem(item, 1);
        cart.addItem(orderItem);

        // Attempt to update quantity with an invalid value
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                cart.updateItemQuantity(orderItem, -1)
        );
        assertEquals("Quantity cannot be negative.", exception.getMessage());
    }
}