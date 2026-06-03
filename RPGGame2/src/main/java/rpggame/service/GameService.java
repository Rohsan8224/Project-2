package rpggame.service;

import rpggame.dao.BattleLogDAO;
import rpggame.dao.PlayerDAO;
import rpggame.dao.PlayerDAO.SavedPlayerRecord;
import rpggame.dao.ShopDAO;
import rpggame.model.*;
import rpggame.util.CombatEngine;
import rpggame.util.EnemyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Central business logic service.
 * Coordinates between the model objects and the DAO/database layer, and acts
 * as the single point of truth used by the GUI panels (the View).
 *
 * <p>Design patterns: this class is the Controller/facade of the MVC
 * architecture, and is also the Subject of the Observer pattern — View
 * components register as {@link GameEventListener}s and are notified of
 * events (e.g. level-ups) without the service depending on any Swing types.</p>
 */
public class GameService {

    private Player currentPlayer;
    private int currentFloor = 1;
    private int currentPlayerId = -1;
    private Enemy currentEnemy;

    private final PlayerDAO playerDAO       = new PlayerDAO();
    private final BattleLogDAO battleLogDAO = new BattleLogDAO();
    private final ShopDAO shopDAO           = new ShopDAO();
    private final Random random             = new Random();

    // Observer pattern: registered listeners notified of game events.
    private final List<GameEventListener> listeners = new ArrayList<>();

    /** Registers an observer to receive game-event notifications. */
    public void addGameEventListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) listeners.add(listener);
    }

    /** Notifies all registered observers of an event. */
    private void fireEvent(String message) {
        for (GameEventListener l : listeners) l.onGameEvent(message);
    }

    // ── Player management ────────────────────────────────────────────────────

    public Player createPlayer(String name, Player.PlayerClass pc) {
        currentPlayer   = new Player(name, pc);
        currentFloor    = 1;
        currentPlayerId = playerDAO.save(currentPlayer, currentFloor);
        return currentPlayer;
    }

    public void loadPlayer(SavedPlayerRecord rec) {
        Player.PlayerClass pc = Player.PlayerClass.valueOf(rec.playerClass());
        currentPlayer = new Player(rec.name(), pc);

        // Use public validated setters — no direct field access needed
        currentPlayer.setMaxHealth(rec.maxHealth());
        currentPlayer.setHealth(rec.health());
        currentPlayer.setAttack(rec.attack());
        currentPlayer.setDefense(rec.defense());
        currentPlayer.setLevel(rec.level());
        currentPlayer.setExperience(rec.experience());

        // Gold: player starts with 50, add the delta to reach saved value
        int delta = rec.gold() - currentPlayer.getGold();
        if (delta > 0)  currentPlayer.addGold(delta);
        else            currentPlayer.spendGold(-delta);

        currentFloor    = rec.dungeonFloor();
        currentPlayerId = rec.id();
    }

    public void saveCurrentPlayer() {
        if (currentPlayer != null && currentPlayerId >= 0) {
            playerDAO.update(currentPlayerId, currentPlayer, currentFloor);
        }
    }

    public List<SavedPlayerRecord> getAllSaves() { return playerDAO.findAll(); }

    public void deleteSave(int playerId) { playerDAO.delete(playerId); }

    // ── Dungeon / floor management ───────────────────────────────────────────

    public Player getCurrentPlayer() { return currentPlayer; }
    public int getCurrentFloor()     { return currentFloor; }
    public boolean isGameWon()       { return currentFloor > 10; }

    /** Returns random room event: "ENEMY", "TREASURE", or "REST". */
    public String exploreRoom() {
        int roll = random.nextInt(10);
        if (roll < 7) return "ENEMY";
        if (roll < 8) return "TREASURE";
        return "REST";
    }

    public int collectTreasure() {
        int gold = 20 + random.nextInt(31);
        currentPlayer.addGold(gold);
        saveCurrentPlayer();
        return gold;
    }

    public int restInRoom() {
        currentPlayer.heal(10);
        saveCurrentPlayer();
        return 10;
    }

    // ── Combat management ────────────────────────────────────────────────────

    public Enemy spawnEnemy() {
        currentEnemy = EnemyFactory.createForFloor(currentFloor);
        return currentEnemy;
    }

    public Enemy getCurrentEnemy() { return currentEnemy; }

    public String playerAttack() {
        return CombatEngine.playerAttacks(currentPlayer, currentEnemy);
    }

    public String playerSpecial() {
        return currentPlayer.useSpecialAbility(currentEnemy);
    }

    public boolean attemptFlee() {
        return CombatEngine.attemptFlee();
    }

    public String useItem(int index) {
        Item item = currentPlayer.getInventory().getItem(index);
        if (item == null) return "No item at that slot.";
        String msg = item.use(currentPlayer);
        currentPlayer.getInventory().removeItem(item);
        return msg;
    }

    public String enemyTurn() {
        if (CombatEngine.enemyUsesSpecial()) {
            return currentEnemy.useSpecialAbility(currentPlayer);
        }
        return CombatEngine.enemyAttacks(currentEnemy, currentPlayer);
    }

    public void endPlayerTurn() {
        currentPlayer.tickCooldown();
    }

    public String processCombatVictory() {
        int xp   = currentEnemy.getXpReward();
        int gold = currentEnemy.getGoldReward();
        int levelsGained = currentPlayer.gainExperience(xp);
        currentPlayer.addGold(gold);
        currentFloor++;
        battleLogDAO.log(currentPlayerId, currentEnemy.getName(), "WIN",
                currentFloor - 1, gold, xp);
        saveCurrentPlayer();
        if (levelsGained > 0) {   // notify observers (Observer pattern)
            fireEvent("LEVEL UP! " + currentPlayer.getName()
                    + " is now level " + currentPlayer.getLevel() + ".");
        }
        return String.format("Victory! Gained %d XP and %d gold.", xp, gold);
    }

    public void processCombatLoss() {
        battleLogDAO.log(currentPlayerId, currentEnemy.getName(), "LOSS",
                currentFloor, 0, 0);
    }

    public void processCombatFlee() {
        battleLogDAO.log(currentPlayerId, currentEnemy.getName(), "FLEE",
                currentFloor, 0, 0);
    }

    // ── Shop management ──────────────────────────────────────────────────────

    public boolean buyItem(Item item) {
        if (currentPlayer.getGold() < item.getCost()) return false;
        if (currentPlayer.getInventory().isFull())    return false;
        currentPlayer.spendGold(item.getCost());
        currentPlayer.getInventory().addItem(cloneItem(item));
        shopDAO.recordPurchase(currentPlayerId, item.getName(), item.getCost());
        saveCurrentPlayer();
        return true;
    }

    private Item cloneItem(Item item) {
        return rpggame.data.ShopData.getShopItems().stream()
                .filter(i -> i.getName().equals(item.getName()))
                .findFirst().orElse(item);
    }

    // ── Battle log ───────────────────────────────────────────────────────────

    public List<String> getBattleLog() {
        if (currentPlayerId < 0) return List.of();
        return battleLogDAO.getLogForPlayer(currentPlayerId);
    }
}
