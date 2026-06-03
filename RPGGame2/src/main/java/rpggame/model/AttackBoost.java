package rpggame.model;

/** Permanently increases the player's attack stat. */
public class AttackBoost extends Item {
    private final int boostAmount;

    public AttackBoost(String name, int boostAmount, int cost) {
        super(name, "Permanently boosts ATK by " + boostAmount, cost);
        this.boostAmount = boostAmount;
    }

    @Override
    public String use(Player player) {
        player.increaseAttack(boostAmount);   // validated mutator, not a public field write
        return player.getName() + " used " + name + "! ATK increased by " + boostAmount + "!";
    }
}
