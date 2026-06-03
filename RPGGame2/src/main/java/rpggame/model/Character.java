package rpggame.model;

/**
 * Interface defining the core behaviours every character in the RPG must
 * provide. Kept deliberately small (Interface Segregation Principle) so that
 * both {@link Player} and {@link Enemy} can be treated polymorphically as a
 * {@code Character} by the combat system.
 */
public interface Character {
    String getName();
    int getHealth();
    int getMaxHealth();
    int getAttack();
    int getDefense();
    boolean isAlive();
    void takeDamage(int damage);
    void heal(int amount);
    String getStatusSummary();
}
