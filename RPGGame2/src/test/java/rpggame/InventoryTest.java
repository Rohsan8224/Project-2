package rpggame;

import org.junit.Before;
import org.junit.Test;
import rpggame.model.*;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for the Inventory class.
 */
public class InventoryTest {

    private Inventory inventory;
    private HealingPotion potion;

    @Before
    public void setUp() {
        inventory = new Inventory();
        potion = new HealingPotion("Test Potion", 30, 20);
    }

    // ── Test 1: New inventory is empty ────────────────────────────────────────
    @Test
    public void testNewInventoryEmpty() {
        assertTrue(inventory.isEmpty());
        assertEquals(0, inventory.getSize());
    }

    // ── Test 2: addItem increases size ────────────────────────────────────────
    @Test
    public void testAddItem() {
        boolean added = inventory.addItem(potion);
        assertTrue(added);
        assertEquals(1, inventory.getSize());
        assertFalse(inventory.isEmpty());
    }

    // ── Test 3: removeItem decreases size ────────────────────────────────────
    @Test
    public void testRemoveItem() {
        inventory.addItem(potion);
        boolean removed = inventory.removeItem(potion);
        assertTrue(removed);
        assertEquals(0, inventory.getSize());
    }

    // ── Test 4: inventory enforces max capacity of 10 ────────────────────────
    @Test
    public void testMaxCapacity() {
        for (int i = 0; i < 10; i++) {
            assertTrue(inventory.addItem(new HealingPotion("P" + i, 10, 5)));
        }
        assertTrue(inventory.isFull());
        // 11th item should be rejected
        assertFalse(inventory.addItem(new HealingPotion("overflow", 10, 5)));
    }

    // ── Test 5: getItem returns correct item by index ─────────────────────────
    @Test
    public void testGetItem() {
        inventory.addItem(potion);
        Item retrieved = inventory.getItem(0);
        assertSame(potion, retrieved);
    }

    // ── Test 6: getItem returns null for out-of-bounds index ─────────────────
    @Test
    public void testGetItemOutOfBounds() {
        assertNull(inventory.getItem(0));
        assertNull(inventory.getItem(-1));
        assertNull(inventory.getItem(99));
    }
}
