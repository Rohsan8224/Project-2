package rpggame;

import org.junit.Before;
import org.junit.Test;
import rpggame.model.Player;
import rpggame.model.Player.PlayerClass;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for the Player model class.
 * Covers: construction, stat defaults, damage, healing, levelling, gold.
 */
public class PlayerTest {

    private Player warrior;
    private Player mage;
    private Player rogue;

    @Before
    public void setUp() {
        warrior = new Player("Thorin", PlayerClass.WARRIOR);
        mage    = new Player("Gandalf", PlayerClass.MAGE);
        rogue   = new Player("Bilbo", PlayerClass.ROGUE);
    }

    // ── Test 1: Player constructed with correct class stats ──────────────────
    @Test
    public void testWarriorStartingStats() {
        assertEquals("Thorin", warrior.getName());
        assertEquals(PlayerClass.WARRIOR, warrior.getPlayerClass());
        assertEquals(120, warrior.getMaxHealth());
        assertEquals(120, warrior.getHealth());
        assertEquals(18,  warrior.getAttack());
        assertEquals(10,  warrior.getDefense());
        assertEquals(1,   warrior.getLevel());
        assertEquals(50,  warrior.getGold());
    }

    // ── Test 2: Different classes have different base stats ──────────────────
    @Test
    public void testClassStatDifferences() {
        // Mage has lowest HP but highest attack
        assertTrue("Mage HP < Warrior HP", mage.getMaxHealth() < warrior.getMaxHealth());
        assertTrue("Mage ATK > Warrior ATK", mage.getAttack() > warrior.getAttack());
        // Warrior has highest defence
        assertTrue("Warrior DEF > Rogue DEF", warrior.getDefense() > rogue.getDefense());
        assertTrue("Warrior DEF > Mage DEF",  warrior.getDefense() > mage.getDefense());
    }

    // ── Test 3: takeDamage reduces health correctly ───────────────────────────
    @Test
    public void testTakeDamage() {
        int before = warrior.getHealth();
        warrior.takeDamage(30); // 30 - 10 (defence) = 20 effective
        assertEquals(before - 20, warrior.getHealth());
    }

    // ── Test 4: takeDamage never drops below zero ─────────────────────────────
    @Test
    public void testTakeDamageFloorZero() {
        warrior.takeDamage(9999);
        assertEquals(0, warrior.getHealth());
        assertFalse(warrior.isAlive());
    }

    // ── Test 5: heal restores HP without exceeding maxHealth ─────────────────
    @Test
    public void testHealDoesNotExceedMax() {
        warrior.takeDamage(30); // lose 20 HP
        warrior.heal(9999);
        assertEquals(warrior.getMaxHealth(), warrior.getHealth());
    }

    // ── Test 6: gainExperience triggers level-up at threshold ────────────────
    @Test
    public void testLevelUp() {
        int levelBefore = warrior.getLevel();
        warrior.gainExperience(100); // XP_PER_LEVEL * level 1 = 100
        assertEquals(levelBefore + 1, warrior.getLevel());
    }

    // ── Test 7: levelling up increases stats ─────────────────────────────────
    @Test
    public void testLevelUpIncreasesStats() {
        int atkBefore = warrior.getAttack();
        int defBefore = warrior.getDefense();
        warrior.gainExperience(100);
        assertTrue(warrior.getAttack()  > atkBefore);
        assertTrue(warrior.getDefense() > defBefore);
    }

    // ── Test 8: gold management ───────────────────────────────────────────────
    @Test
    public void testGoldManagement() {
        warrior.addGold(100);
        assertEquals(150, warrior.getGold());
        assertTrue(warrior.spendGold(80));
        assertEquals(70, warrior.getGold());
    }

    // ── Test 9: cannot spend more gold than available ─────────────────────────
    @Test
    public void testCannotOverspendGold() {
        assertFalse(warrior.spendGold(1000));
        assertEquals(50, warrior.getGold()); // unchanged
    }

    // ── Test 10: special ability cooldown ticks correctly ────────────────────
    @Test
    public void testSpecialCooldownTick() {
        // Use special (enemy needed — use self as dummy target for test)
        warrior.useSpecialAbility(mage);
        assertEquals(3, warrior.getSpecialCooldown());
        warrior.tickCooldown();
        assertEquals(2, warrior.getSpecialCooldown());
        warrior.tickCooldown();
        warrior.tickCooldown();
        assertEquals(0, warrior.getSpecialCooldown());
    }
}
