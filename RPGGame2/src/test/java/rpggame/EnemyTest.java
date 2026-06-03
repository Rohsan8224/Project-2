package rpggame;

import org.junit.Test;
import rpggame.model.Enemy;
import rpggame.model.Enemy.EnemyType;
import rpggame.model.Player;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for the Enemy model class.
 */
public class EnemyTest {

    // ── Test 1: Enemy created with correct type stats ─────────────────────────
    @Test
    public void testGoblinStats() {
        Enemy goblin = new Enemy(EnemyType.GOBLIN);
        assertEquals("Goblin", goblin.getName());
        assertEquals(40,  goblin.getMaxHealth());
        assertEquals(8,   goblin.getAttack());
        assertEquals(2,   goblin.getDefense());
        assertEquals(30,  goblin.getXpReward());
        assertEquals(10,  goblin.getGoldReward());
    }

    // ── Test 2: Dragon boss has highest stats ─────────────────────────────────
    @Test
    public void testDragonBossIsStrongest() {
        Enemy goblin = new Enemy(EnemyType.GOBLIN);
        Enemy dragon = new Enemy(EnemyType.DRAGON_BOSS);
        assertTrue(dragon.getMaxHealth() > goblin.getMaxHealth());
        assertTrue(dragon.getAttack()    > goblin.getAttack());
        assertTrue(dragon.getXpReward()  > goblin.getXpReward());
        assertTrue(dragon.getGoldReward()> goblin.getGoldReward());
    }

    // ── Test 3: Enemy dies when HP reaches 0 ─────────────────────────────────
    @Test
    public void testEnemyDeath() {
        Enemy orc = new Enemy(EnemyType.ORC);
        assertTrue(orc.isAlive());
        orc.takeDamage(9999);
        assertFalse(orc.isAlive());
        assertEquals(0, orc.getHealth());
    }

    // ── Test 4: Enemy special ability executes without error ─────────────────
    @Test
    public void testSpecialAbilityReturnsMessage() {
        Enemy troll = new Enemy(EnemyType.TROLL);
        Player player = new Player("Hero", Player.PlayerClass.WARRIOR);
        String result = troll.useSpecialAbility(player);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ── Test 5: Enemy defence reduces incoming damage ─────────────────────────
    @Test
    public void testEnemyDefenceReducesDamage() {
        Enemy troll = new Enemy(EnemyType.TROLL); // DEF = 8
        int before = troll.getHealth();
        troll.takeDamage(20); // effective = 20 - 8 = 12
        assertEquals(before - 12, troll.getHealth());
    }
}
