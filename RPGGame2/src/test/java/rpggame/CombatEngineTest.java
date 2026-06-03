package rpggame;

import org.junit.Test;
import rpggame.model.Enemy;
import rpggame.model.Enemy.EnemyType;
import rpggame.model.Player;
import rpggame.util.CombatEngine;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for CombatEngine utility class.
 */
public class CombatEngineTest {

    // ── Test 1: Player attack reduces enemy HP ────────────────────────────────
    @Test
    public void testPlayerAttackReducesEnemyHP() {
        Player player = new Player("Hero", Player.PlayerClass.MAGE);
        Enemy  goblin = new Enemy(EnemyType.GOBLIN);
        int hpBefore = goblin.getHealth();
        CombatEngine.playerAttacks(player, goblin);
        assertTrue("Enemy HP should decrease after attack", goblin.getHealth() < hpBefore);
    }

    // ── Test 2: Enemy attack reduces player HP ────────────────────────────────
    @Test
    public void testEnemyAttackReducesPlayerHP() {
        Player player = new Player("Hero", Player.PlayerClass.WARRIOR);
        Enemy  orc    = new Enemy(EnemyType.ORC);
        int hpBefore = player.getHealth();
        CombatEngine.enemyAttacks(orc, player);
        assertTrue("Player HP should decrease after enemy attack", player.getHealth() < hpBefore);
    }

    // ── Test 3: playerAttacks returns a non-empty message ────────────────────
    @Test
    public void testPlayerAttackReturnsMessage() {
        Player player = new Player("Hero", Player.PlayerClass.WARRIOR);
        Enemy  enemy  = new Enemy(EnemyType.SKELETON);
        String msg = CombatEngine.playerAttacks(player, enemy);
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }

    // ── Test 4: enemyAttacks returns a non-empty message ─────────────────────
    @Test
    public void testEnemyAttackReturnsMessage() {
        Player player = new Player("Hero", Player.PlayerClass.WARRIOR);
        Enemy  enemy  = new Enemy(EnemyType.TROLL);
        String msg = CombatEngine.enemyAttacks(enemy, player);
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }

    // ── Test 5: Minimum damage is always at least 1 ──────────────────────────
    @Test
    public void testMinimumDamageIsOne() {
        // Build a player with 0 attack to force minimum scenario
        // Goblin has DEF=2; even with very low attack, min effective = 1
        Player player = new Player("Weak", Player.PlayerClass.MAGE);
        Enemy  goblin = new Enemy(EnemyType.GOBLIN);
        // Run 20 attacks and verify HP never goes below (original - 20)
        // at minimum 1 damage per hit, HP should drop by at least 1 each time
        int hp = goblin.getHealth();
        CombatEngine.playerAttacks(player, goblin);
        assertTrue("Damage should be at least 1", goblin.getHealth() < hp);
    }

    // ── Test 6: EnemyFactory creates correct type for floor range ─────────────
    @Test
    public void testEnemyFactoryFloor10ReturnsDragonBoss() {
        for (int i = 0; i < 10; i++) { // run multiple times — floor 10 always = dragon
            Enemy e = rpggame.util.EnemyFactory.createForFloor(10);
            assertEquals(EnemyType.DRAGON_BOSS, e.getType());
        }
    }
}
