package rpggame.model;

/** A consumable that restores a fixed amount of HP (capped at max HP). */
public class HealingPotion extends Item {
    private final int healAmount;

    public HealingPotion(String name, int healAmount, int cost) {
        super(name, "Restores " + healAmount + " HP", cost);
        this.healAmount = healAmount;
    }

    @Override
    public String use(Player player) {
        player.heal(healAmount);
        return player.getName() + " used " + name + " and recovered up to " + healAmount + " HP!";
    }
}
