package rpggame.model;

/**
 * A player-controlled character. Players have a class (Warrior/Mage/Rogue),
 * a level, experience, gold, an {@link Inventory} and a special-ability
 * cooldown. Extends {@link AbstractEntity} and overrides
 * {@link #useSpecialAbility(AbstractEntity)} with class-specific behaviour
 * (polymorphism). All mutable state is encapsulated and changed only through
 * validated methods.
 */
public class Player extends AbstractEntity {

    public enum PlayerClass { WARRIOR, MAGE, ROGUE }

    private final PlayerClass playerClass;
    private int level;
    private int experience;
    private int gold;
    private final Inventory inventory;
    private int specialCooldown;          // turns until the special can be reused

    private static final int XP_PER_LEVEL  = 100;
    private static final int STARTING_GOLD = 50;

    public Player(String name, PlayerClass playerClass) {
        super(name, baseHealth(playerClass), baseAttack(playerClass), baseDefense(playerClass));
        this.playerClass     = playerClass;
        this.level           = 1;
        this.experience      = 0;
        this.gold            = STARTING_GOLD;
        this.inventory       = new Inventory();
        this.specialCooldown = 0;
    }

    // Class base statistics (FR-02)
    private static int baseHealth(PlayerClass pc) {
        return switch (pc) { case WARRIOR -> 120; case MAGE -> 80; case ROGUE -> 100; };
    }
    private static int baseAttack(PlayerClass pc) {
        return switch (pc) { case WARRIOR -> 18; case MAGE -> 25; case ROGUE -> 22; };
    }
    private static int baseDefense(PlayerClass pc) {
        return switch (pc) { case WARRIOR -> 10; case MAGE -> 3; case ROGUE -> 6; };
    }

    @Override
    public String useSpecialAbility(AbstractEntity target) {
        if (specialCooldown > 0) {
            return getName() + "'s special ability is on cooldown (" + specialCooldown + " turns left)!";
        }
        specialCooldown = 3;
        return switch (playerClass) {
            case WARRIOR -> {
                int dmg = getAttack() * 2 + getDefense();
                target.takeDamage(dmg);
                yield getName() + " uses SHIELD BASH! Deals heavy damage and stuns!";
            }
            case MAGE -> {
                int dmg = getAttack() * 3;
                target.takeDamage(dmg);
                yield getName() + " casts FIREBALL! Deals massive " + dmg + " magic damage!";
            }
            case ROGUE -> {
                int dmg = (int) (getAttack() * 2.5);
                target.takeDamage(dmg);
                yield getName() + " performs BACKSTAB! Deals " + dmg + " critical damage!";
            }
        };
    }

    /**
     * Awards experience and triggers level-ups while the threshold is met.
     * @return the number of levels gained (0 if none) so the service layer can
     *         fire a level-up notification (Observer pattern).
     */
    public int gainExperience(int xp) {
        if (xp <= 0) return 0;
        int levelsGained = 0;
        experience += xp;
        while (experience >= XP_PER_LEVEL * level) {
            experience -= XP_PER_LEVEL * level;
            levelUp();
            levelsGained++;
        }
        return levelsGained;
    }

    // Increases stats on level-up via validated mutators (FR-09)
    private void levelUp() {
        level++;
        increaseMaxHealth(20);
        heal(20);
        increaseAttack(3);
        increaseDefense(2);
    }

    public void tickCooldown() { if (specialCooldown > 0) specialCooldown--; }

    // Accessors
    public int getLevel()               { return level; }
    public int getExperience()          { return experience; }
    public int getXpToNextLevel()       { return XP_PER_LEVEL * level; }
    public int getGold()                { return gold; }
    public Inventory getInventory()     { return inventory; }
    public PlayerClass getPlayerClass() { return playerClass; }
    public int getSpecialCooldown()     { return specialCooldown; }

    // Validated mutators (used by the service when restoring a DB save)
    public void addGold(int amount) { if (amount > 0) gold += amount; }
    public boolean spendGold(int amount) {
        if (amount < 0 || gold < amount) return false;
        gold -= amount;
        return true;
    }
    public void setLevel(int lvl)     { this.level      = Math.max(1, lvl); }
    public void setExperience(int xp) { this.experience = Math.max(0, xp); }

    @Override
    public String getStatusSummary() {
        return String.format(
            "[%s - Lv%d %s]  HP %d/%d | ATK %d | DEF %d | Gold %d | XP %d/%d | Special CD %d",
            getName(), level, playerClass, getHealth(), getMaxHealth(),
            getAttack(), getDefense(), gold, experience, getXpToNextLevel(), specialCooldown);
    }
}
