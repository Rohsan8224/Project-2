package rpggame;

import org.junit.*;
import rpggame.dao.BattleLogDAO;
import rpggame.dao.PlayerDAO;
import rpggame.dao.PlayerDAO.SavedPlayerRecord;
import rpggame.db.DatabaseManager;
import rpggame.model.Player;

import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for database operations via DAO layer.
 * Uses the real embedded Derby database (in-process, no external setup needed).
 */
public class DatabaseTest {

    private PlayerDAO playerDAO;
    private BattleLogDAO battleLogDAO;
    private int testPlayerId = -1;

    @Before
    public void setUp() {
        DatabaseManager.getInstance(); // ensure DB is initialised
        playerDAO    = new PlayerDAO();
        battleLogDAO = new BattleLogDAO();
    }

    @After
    public void tearDown() {
        // Clean up any test player created during this test run
        if (testPlayerId >= 0) {
            playerDAO.delete(testPlayerId);
            testPlayerId = -1;
        }
    }

    // ── Test 1: Player can be saved to the database ───────────────────────────
    @Test
    public void testSavePlayer() {
        Player p = new Player("DBTestWarrior", Player.PlayerClass.WARRIOR);
        testPlayerId = playerDAO.save(p, 1);
        assertTrue("Player ID should be > 0 after save", testPlayerId > 0);
    }

    // ── Test 2: Saved player appears in findAll() ────────────────────────────
    @Test
    public void testFindAllContainsSavedPlayer() {
        Player p = new Player("DBTestMage", Player.PlayerClass.MAGE);
        testPlayerId = playerDAO.save(p, 3);

        List<SavedPlayerRecord> all = playerDAO.findAll();
        boolean found = all.stream().anyMatch(r -> r.id() == testPlayerId);
        assertTrue("Saved player should appear in findAll()", found);
    }

    // ── Test 3: Saved player record has correct field values ─────────────────
    @Test
    public void testSavedPlayerFields() {
        Player p = new Player("DBTestRogue", Player.PlayerClass.ROGUE);
        testPlayerId = playerDAO.save(p, 5);

        List<SavedPlayerRecord> all = playerDAO.findAll();
        SavedPlayerRecord rec = all.stream()
                .filter(r -> r.id() == testPlayerId)
                .findFirst()
                .orElse(null);

        assertNotNull("Record should exist", rec);
        assertEquals("DBTestRogue", rec.name());
        assertEquals("ROGUE",       rec.playerClass());
        assertEquals(5,             rec.dungeonFloor());
        assertEquals(p.getGold(),   rec.gold());
    }

    // ── Test 4: Player record can be updated ─────────────────────────────────
    @Test
    public void testUpdatePlayer() {
        Player p = new Player("DBTestUpdate", Player.PlayerClass.WARRIOR);
        testPlayerId = playerDAO.save(p, 1);

        p.addGold(200);
        playerDAO.update(testPlayerId, p, 4);

        List<SavedPlayerRecord> all = playerDAO.findAll();
        SavedPlayerRecord rec = all.stream()
                .filter(r -> r.id() == testPlayerId)
                .findFirst()
                .orElse(null);

        assertNotNull(rec);
        assertEquals(250, rec.gold());         // 50 + 200
        assertEquals(4,   rec.dungeonFloor());
    }

    // ── Test 5: Player can be deleted from the database ──────────────────────
    @Test
    public void testDeletePlayer() {
        Player p = new Player("DBTestDelete", Player.PlayerClass.MAGE);
        testPlayerId = playerDAO.save(p, 1);

        playerDAO.delete(testPlayerId);
        testPlayerId = -1; // already deleted, don't try again in tearDown

        List<SavedPlayerRecord> all = playerDAO.findAll();
        boolean stillExists = all.stream().anyMatch(r -> r.name().equals("DBTestDelete"));
        assertFalse("Deleted player should not appear in findAll()", stillExists);
    }

    // ── Test 6: Battle log records are stored and retrieved ──────────────────
    @Test
    public void testBattleLogEntry() {
        Player p = new Player("DBTestBattle", Player.PlayerClass.ROGUE);
        testPlayerId = playerDAO.save(p, 2);

        battleLogDAO.log(testPlayerId, "Goblin", "WIN", 2, 10, 30);

        List<String> log = battleLogDAO.getLogForPlayer(testPlayerId);
        assertFalse("Battle log should not be empty", log.isEmpty());
        assertTrue("Log entry should mention Goblin",
                log.stream().anyMatch(s -> s.contains("Goblin")));
        assertTrue("Log entry should mention WIN",
                log.stream().anyMatch(s -> s.contains("WIN")));
    }
}
