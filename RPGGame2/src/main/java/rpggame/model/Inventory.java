package rpggame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a player's collection of items, demonstrating use of the
 * collections framework ({@code ArrayList<Item>}). Enforces the 10-item
 * capacity described in FR-11.
 */
public class Inventory {
    public static final int MAX_SIZE = 10;
    private final List<Item> items = new ArrayList<>();

    public boolean addItem(Item item) {
        if (item == null || items.size() >= MAX_SIZE) return false;
        return items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) return null;
        return items.get(index);
    }

    /** Returns a defensive copy so callers cannot mutate the backing list. */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int getSize()      { return items.size(); }
    public boolean isFull()   { return items.size() >= MAX_SIZE; }
    public boolean isEmpty()  { return items.isEmpty(); }
}
