package rpggame.data;

import rpggame.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the list of items available in the in-game shop.
 * Acts as a simple data access layer for shop inventory.
 */
public class ShopData {

    /**
     * Returns all items that can be purchased from the shop.
     */
    public static List<Item> getShopItems() {
        List<Item> items = new ArrayList<>();
        items.add(new HealingPotion("Minor Potion",  30, 20));
        items.add(new HealingPotion("Major Potion",  70, 45));
        items.add(new HealingPotion("Full Restore",  999, 100));
        items.add(new AttackBoost("Iron Sword",      5, 60));
        items.add(new AttackBoost("Steel Blade",     10, 110));
        items.add(new DefenseBoost("Leather Armour", 5, 55));
        items.add(new DefenseBoost("Iron Shield",    10, 100));
        return items;
    }
}
