package rpggame;

import org.junit.Before;
import org.junit.Test;
import rpggame.model.*;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for Item subclasses: HealingPotion, AttackBoost, DefenseBoost.
 */
public class ItemTest {

    private Player player;

    @Before
    public void setUp() {
        player = new Player("Hero", Player.PlayerClass.WARRIOR);
        player.takeDamage(50); // deal 40 actual damage (50 - 10 DEF)
    }

    // ── Test 1: HealingPotion restores correct HP ─────────────────────────────
    @Test
    public void testHealingPotionRestoresHP() {
        int hpBefore = player.getHealth();
        HealingPotion potion = new HealingPotion("Minor Potion", 30, 20);
        potion.use(player);
        assertEquals(hpBefore + 30, player.getHealth());
    }

    // ── Test 2: HealingPotion does not exceed max health ─────────────────────
    @Test
    public void testHealingPotionCapped() {
        HealingPotion bigPotion = new HealingPotion("Full Restore", 9999, 100);
        bigPotion.use(player);
        assertEquals(player.getMaxHealth(), player.getHealth());
    }

    // ── Test 3: AttackBoost permanently increases attack ─────────────────────
    @Test
    public void testAttackBoostIncreasesAttack() {
        int atkBefore = player.getAttack();
        AttackBoost boost = new AttackBoost("Iron Sword", 5, 60);
        boost.use(player);
        assertEquals(atkBefore + 5, player.getAttack());
    }

    // ── Test 4: DefenseBoost permanently increases defence ───────────────────
    @Test
    public void testDefenseBoostIncreasesDefence() {
        int defBefore = player.getDefense();
        DefenseBoost boost = new DefenseBoost("Leather Armour", 5, 55);
        boost.use(player);
        assertEquals(defBefore + 5, player.getDefense());
    }

    // ── Test 5: Item use() returns non-null descriptive message ──────────────
    @Test
    public void testItemUseReturnsMessage() {
        HealingPotion p = new HealingPotion("Potion", 20, 10);
        String msg = p.use(player);
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
        assertTrue(msg.contains("20")); // should mention the heal amount
    }

    // ── Test 6: Item cost and name are set correctly ──────────────────────────
    @Test
    public void testItemProperties() {
        AttackBoost sword = new AttackBoost("Steel Blade", 10, 110);
        assertEquals("Steel Blade", sword.getName());
        assertEquals(110, sword.getCost());
    }
}
