package rpggame.util;

import rpggame.model.AbstractEntity;
import rpggame.model.Enemy;
import rpggame.model.Player;

import java.util.Random;

/**
 * Handles all combat calculations between player and enemy.
 * Extracted into its own class for SRP and testability.
 */
public class CombatEngine {
    private static final Random random = new Random();

    /**
     * Player performs a basic attack on the enemy.
     * Returns a description of what happened.
     */
    public static String playerAttacks(Player player, Enemy enemy) {
        boolean critical = random.nextInt(10) == 0; // 10% crit chance
        int rawDamage = critical ? player.getAttack() * 2 : player.getAttack();
        int roll = random.nextInt(5) - 2; // small random variation
        int finalDamage = Math.max(1, rawDamage + roll);
        enemy.takeDamage(finalDamage);
        String critText = critical ? " [CRITICAL HIT!]" : "";
        return String.format("%s attacks %s for %d damage!%s",
                player.getName(), enemy.getName(), finalDamage, critText);
    }

    /**
     * Enemy performs a basic attack on the player.
     */
    public static String enemyAttacks(Enemy enemy, Player player) {
        boolean critical = random.nextInt(8) == 0; // 12.5% crit for enemies
        int rawDamage = critical ? enemy.getAttack() * 2 : enemy.getAttack();
        int roll = random.nextInt(4) - 1;
        int finalDamage = Math.max(1, rawDamage + roll);
        player.takeDamage(finalDamage);
        String critText = critical ? " [CRITICAL!]" : "";
        return String.format("%s attacks %s for %d damage!%s",
                enemy.getName(), player.getName(), finalDamage, critText);
    }

    /**
     * Determines if the player successfully flees (50% base chance).
     */
    public static boolean attemptFlee() {
        return random.nextBoolean();
    }

    /**
     * Decides whether the enemy uses its special ability this turn (30% chance).
     */
    public static boolean enemyUsesSpecial() {
        return random.nextInt(10) < 3;
    }
}
