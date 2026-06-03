package rpggame.model;

/** Permanently increases the player's defence stat. */
public class DefenseBoost extends Item {
    private final int boostAmount;

    public DefenseBoost(String name, int boostAmount, int cost) {
        super(name, "Permanently boosts DEF by " + boostAmount, cost);
        this.boostAmount = boostAmount;
    }

    @Override
    public String use(Player player) {
        player.increaseDefense(boostAmount);  // validated mutator, not a public field write
        return player.getName() + " used " + name + "! DEF increased by " + boostAmount + "!";
    }
}
