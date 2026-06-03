package rpggame.gui;

import rpggame.model.Enemy;
import rpggame.model.Item;
import rpggame.model.Player;
import rpggame.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Turn-based combat screen.
 */
public class CombatPanel extends JPanel {

    private final GameService service;
    private final MainApp app;

    private final JProgressBar playerHP = GUIHelper.makeHPBar(100, 100);
    private final JProgressBar enemyHP  = GUIHelper.makeHPBar(100, 100);
    private final JLabel playerName = GUIHelper.makeLabel("Player");
    private final JLabel enemyName  = GUIHelper.makeLabel("Enemy");
    private final JTextArea combatLog = GUIHelper.makeLogArea();
    private final JButton specialBtn = GUIHelper.makeButton("Special Ability", new Color(130, 60, 160));
    private final JButton attackBtn  = GUIHelper.makeButton("Attack",           new Color(160, 60, 60));
    private final JButton itemBtn    = GUIHelper.makeButton("Use Item",         new Color(60, 120, 60));
    private final JButton fleeBtn    = GUIHelper.makeButton("Flee",             new Color(80, 80, 80));

    public CombatPanel(MainApp app, GameService service) {
        this.app = app;
        this.service = service;
        setLayout(new BorderLayout(8, 8));
        setBackground(GUIHelper.BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildLog(),    BorderLayout.CENTER);
        add(buildActions(),BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(GUIHelper.BG_DARK);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(buildSide(playerName, playerHP, true));
        panel.add(buildSide(enemyName,  enemyHP,  false));
        return panel;
    }

    private JPanel buildSide(JLabel nameLabel, JProgressBar bar, boolean isPlayer) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(GUIHelper.BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isPlayer ? GUIHelper.ACCENT : GUIHelper.HP_RED, 1),
                new EmptyBorder(8, 12, 8, 12)));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(isPlayer ? GUIHelper.ACCENT : GUIHelper.HP_RED);
        bar.setPreferredSize(new Dimension(300, 24));
        p.add(nameLabel, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane buildLog() {
        JScrollPane scroll = new JScrollPane(combatLog);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        return scroll;
    }

    private JPanel buildActions() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setBackground(GUIHelper.BG_DARK);
        row.setBorder(new EmptyBorder(10, 0, 0, 0));

        attackBtn .addActionListener(e -> handleAttack());
        specialBtn.addActionListener(e -> handleSpecial());
        itemBtn   .addActionListener(e -> handleItem());
        fleeBtn   .addActionListener(e -> handleFlee());

        row.add(attackBtn);
        row.add(specialBtn);
        row.add(itemBtn);
        row.add(fleeBtn);
        return row;
    }

    // ── Combat actions ───────────────────────────────────────────────────────

    private void handleAttack() {
        log(service.playerAttack());
        service.endPlayerTurn();
        checkEnemyDead();
    }

    private void handleSpecial() {
        Player p = service.getCurrentPlayer();
        if (p.getSpecialCooldown() > 0) {
            log("Special ability on cooldown! (" + p.getSpecialCooldown() + " turns)");
            return;
        }
        log(service.playerSpecial());
        service.endPlayerTurn();
        checkEnemyDead();
    }

    private void handleItem() {
        Player p = service.getCurrentPlayer();
        if (p.getInventory().isEmpty()) { log("No items!"); return; }
        List<Item> items = p.getInventory().getItems();
        String[] opts = items.stream().map(i -> i.getName() + " — " + i.getDescription()).toArray(String[]::new);
        int choice = JOptionPane.showOptionDialog(app, "Choose item:", "Use Item",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (choice >= 0) {
            log(service.useItem(choice));
            service.endPlayerTurn();
            enemyTurn();
            refreshBars();
        }
    }

    private void handleFlee() {
        if (service.attemptFlee()) {
            service.processCombatFlee();
            log("You fled successfully!");
            returnToDungeon();
        } else {
            log("Couldn't escape!");
            service.endPlayerTurn();
            enemyTurn();
            refreshBars();
        }
    }

    private void checkEnemyDead() {
        Enemy enemy = service.getCurrentEnemy();
        if (!enemy.isAlive()) {
            String result = service.processCombatVictory();
            log(result);
            setActionsEnabled(false);
            JOptionPane.showMessageDialog(app, result, "Victory!", JOptionPane.PLAIN_MESSAGE);
            returnToDungeon();
            return;
        }
        enemyTurn();
        refreshBars();
    }

    private void enemyTurn() {
        log(service.enemyTurn());
        Player p = service.getCurrentPlayer();
        if (!p.isAlive()) {
            log("You have been defeated...");
            service.processCombatLoss();
            setActionsEnabled(false);
            JOptionPane.showMessageDialog(app, "You were defeated! Game Over.", "Defeated", JOptionPane.ERROR_MESSAGE);
            app.showScreen(MainApp.SCREEN_MENU);
        }
    }

    private void returnToDungeon() {
        app.showScreen(MainApp.SCREEN_DUNGEON);
    }

    // ── UI helpers ───────────────────────────────────────────────────────────

    public void refresh() {
        combatLog.setText("");
        Player p = service.getCurrentPlayer();
        Enemy  e = service.getCurrentEnemy();
        if (p == null || e == null) return;

        playerName.setText(p.getName() + " (Lv" + p.getLevel() + ")");
        enemyName .setText(e.getName());

        updateBar(playerHP, p.getHealth(), p.getMaxHealth());
        updateBar(enemyHP,  e.getHealth(), e.getMaxHealth());
        setActionsEnabled(true);
        log("A wild " + e.getName() + " appears! Prepare for battle!");
        specialBtn.setText("Special" + (p.getSpecialCooldown() > 0 ? " (CD:" + p.getSpecialCooldown() + ")" : ""));
    }

    private void refreshBars() {
        Player p = service.getCurrentPlayer();
        Enemy  e = service.getCurrentEnemy();
        updateBar(playerHP, p.getHealth(), p.getMaxHealth());
        updateBar(enemyHP,  e.getHealth(), e.getMaxHealth());
        specialBtn.setText("Special" + (p.getSpecialCooldown() > 0 ? " (CD:" + p.getSpecialCooldown() + ")" : ""));
    }

    private void updateBar(JProgressBar bar, int val, int max) {
        bar.setMaximum(max);
        bar.setValue(val);
        bar.setString(val + " / " + max);
        double pct = (double) val / max;
        bar.setForeground(pct > 0.5 ? GUIHelper.HP_GREEN : pct > 0.25 ? Color.ORANGE : GUIHelper.HP_RED);
    }

    private void log(String msg) {
        combatLog.append("> " + msg + "\n");
        combatLog.setCaretPosition(combatLog.getDocument().getLength());
    }

    private void setActionsEnabled(boolean en) {
        attackBtn.setEnabled(en);
        specialBtn.setEnabled(en);
        itemBtn.setEnabled(en);
        fleeBtn.setEnabled(en);
    }
}
