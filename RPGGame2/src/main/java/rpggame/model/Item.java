package rpggame.model;

/**
 * Abstract base for all inventory items. Concrete subclasses
 * ({@link HealingPotion}, {@link AttackBoost}, {@link DefenseBoost}) each
 * implement {@link #use(Player)} polymorphically — there is no type switch,
 * satisfying the Open/Closed Principle (new item types extend Item without
 * modifying existing code).
 */
public abstract class Item {

    protected final String name;
    protected final String description;
    protected final int cost;

    protected Item(String name, String description, int cost) {
        this.name        = name;
        this.description = description;
        this.cost        = Math.max(0, cost);
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public int getCost()           { return cost; }

    /**
     * Applies this item's effect to the given player.
     * @return a human-readable message describing the effect.
     */
    public abstract String use(Player player);

    @Override
    public String toString() {
        return String.format("%-16s | %-28s | Cost: %d gold", name, description, cost);
    }
}
