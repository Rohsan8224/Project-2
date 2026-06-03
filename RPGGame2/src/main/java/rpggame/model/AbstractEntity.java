package rpggame.model;

/**
 * Abstract base class for all game entities (players and enemies).
 * Implements the shared logic for health, combat damage and status display.
 *
 * <p>Encapsulation: all mutable state ({@code health}, {@code maxHealth},
 * {@code attack}, {@code defense}, {@code name}) is declared {@code private}.
 * State is changed only through validated accessors and the {@code protected}
 * mutators below, so no caller can place the entity into an illegal state
 * (e.g. negative health or health above maximum).</p>
 */
public abstract class AbstractEntity implements Character {

    private String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;

    protected AbstractEntity(String name, int maxHealth, int attack, int defense) {
        this.name      = name;
        this.maxHealth = Math.max(1, maxHealth);
        this.health    = this.maxHealth;
        this.attack    = Math.max(0, attack);
        this.defense   = Math.max(0, defense);
    }

    // ── Read-only accessors (the Character contract) ─────────────────────────
    @Override public String getName()   { return name; }
    @Override public int getHealth()    { return health; }
    @Override public int getMaxHealth() { return maxHealth; }
    @Override public int getAttack()    { return attack; }
    @Override public int getDefense()   { return defense; }
    @Override public boolean isAlive()  { return health > 0; }

    // ── Validated setters (used when restoring a saved game) ─────────────────
    // Each guards against illegal values so encapsulation cannot be bypassed.
    public void setMaxHealth(int m) {
        this.maxHealth = Math.max(1, m);
        if (health > maxHealth) health = maxHealth;
    }
    public void setHealth(int h) {
        this.health = Math.max(0, Math.min(h, maxHealth));
    }
    public void setAttack(int a)  { this.attack  = Math.max(0, a); }
    public void setDefense(int d) { this.defense = Math.max(0, d); }

    // ── Protected mutators for subclass/same-package use ─────────────────────
    // Replace the previous direct public-field writes (e.g. levelling, boosts).
    protected void setName(String name)      { this.name = name; }
    protected void increaseMaxHealth(int amt){ if (amt > 0) maxHealth += amt; }
    protected void increaseAttack(int amt)   { if (amt > 0) attack    += amt; }
    protected void increaseDefense(int amt)  { if (amt > 0) defense   += amt; }

    @Override
    public void takeDamage(int damage) {
        int effective = Math.max(1, damage - defense); // always at least 1
        health = Math.max(0, health - effective);
    }

    @Override
    public void heal(int amount) {
        if (amount <= 0) return;
        health = Math.min(maxHealth, health + amount);
    }

    @Override
    public String getStatusSummary() {
        return String.format("%s | HP: %d/%d | ATK: %d | DEF: %d",
                name, health, maxHealth, attack, defense);
    }

    /** Each concrete entity defines its own special ability (polymorphism). */
    public abstract String useSpecialAbility(AbstractEntity target);
}
