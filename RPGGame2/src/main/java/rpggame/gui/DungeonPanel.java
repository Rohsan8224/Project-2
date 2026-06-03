package rpggame.gui;

import rpggame.model.Item;
import rpggame.model.Player;
import rpggame.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Main dungeon hub screen — shows player stats and action buttons.
 */
public class DungeonPanel extends JPanel {

    private final GameService service;
    private final MainApp app;

    // Stat labels
    private final JLabel nameLabel  = GUIHelper.makeLabel("");
    private final JLabel classLabel = GUIHelper.makeLabel("");
    private final JLabel levelLabel = GUIHelper.makeLabel("");
    private final JLabel goldLabel  = GUIHelper.makeLabel("");
    private final JLabel floorLabel = GUIHelper.makeLabel("");
    private final JProgressBar hpBar = GUIHelper.makeHPBar(100, 100);
    private final JTextArea inventoryArea = GUIHelper.makeLogArea();
    private final JTextArea eventLog = GUIHelper.makeLogArea();

    public DungeonPanel(MainApp app, GameService service) {
        this.app = app;
        this.service = service;
        // Observer pattern: receive level-up / event notifications from the service.
        service.addGameEventListener(this::appendLog);
        setLayout(new BorderLayout(8, 8));
        setBackground(GUIHelper.BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCentre(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.EAST);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        top.setBackground(new Color(20, 20, 35));
        top.setBorder(BorderFactory.createLineBorder(GUIHelper.ACCENT, 1));
        floorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        floorLabel.setForeground(GUIHelper.GOLD_COLOR);
        top.add(floorLabel);
        top.add(nameLabel);
        top.add(classLabel);
        top.add(levelLabel);
        top.add(goldLabel);
        return top;
    }

    private JPanel buildCentre() {
        JPanel centre = new JPanel(new BorderLayout(8, 8));
        centre.setBackground(GUIHelper.BG_DARK);

        // HP bar panel
        JPanel hpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        hpPanel.setBackground(GUIHelper.BG_PANEL);
        hpPanel.add(GUIHelper.makeLabel("HP:"));
        hpBar.setPreferredSize(new Dimension(300, 24));
        hpPanel.add(hpBar);

        // Event log
        JScrollPane logScroll = new JScrollPane(eventLog);
        logScroll.setBorder(titledBorder("Event Log"));
        logScroll.setPreferredSize(new Dimension(400, 250));

        // Inventory
        JScrollPane invScroll = new JScrollPane(inventoryArea);
        invScroll.setBorder(titledBorder("Inventory"));

        JPanel leftCol = new JPanel(new BorderLayout(0, 8));
        leftCol.setBackground(GUIHelper.BG_DARK);
        leftCol.add(hpPanel, BorderLayout.NORTH);
        leftCol.add(logScroll, BorderLayout.CENTER);
        leftCol.add(invScroll, BorderLayout.SOUTH);

        centre.add(leftCol, BorderLayout.CENTER);
        return centre;
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel(new GridLayout(6, 1, 0, 10));
        actions.setBackground(GUIHelper.BG_DARK);
        actions.setBorder(new EmptyBorder(0, 8, 0, 0));
        actions.setPreferredSize(new Dimension(180, 0));

        JButton explore = GUIHelper.makeButton("Explore Room",    new Color(50, 100, 160));
        JButton shop    = GUIHelper.makeButton("Visit Shop",       new Color(130, 100, 30));
        JButton useItem = GUIHelper.makeButton("Use Item",         new Color(60, 100, 70));
        JButton log     = GUIHelper.makeButton("Battle Log",       new Color(80, 60, 120));
        JButton save    = GUIHelper.makeButton("Save Game",        new Color(50, 80, 100));
        JButton menu    = GUIHelper.makeButton("Main Menu",        new Color(100, 50, 50));

        explore.addActionListener(e -> handleExplore());
        shop   .addActionListener(e -> app.showScreen(MainApp.SCREEN_SHOP));
        useItem.addActionListener(e -> handleUseItem());
        log    .addActionListener(e -> app.showScreen(MainApp.SCREEN_LOG));
        save   .addActionListener(e -> {
            service.saveCurrentPlayer();
            appendLog("Game saved!");
        });
        menu.addActionListener(e -> {
            service.saveCurrentPlayer();
            app.showScreen(MainApp.SCREEN_MENU);
        });

        actions.add(explore);
        actions.add(shop);
        actions.add(useItem);
        actions.add(log);
        actions.add(save);
        actions.add(menu);
        return actions;
    }

    private void handleExplore() {
        String event = service.exploreRoom();
        switch (event) {
            case "ENEMY" -> {
                service.spawnEnemy();
                appendLog("A " + service.getCurrentEnemy().getName() + " appears!");
                app.showScreen(MainApp.SCREEN_COMBAT);
            }
            case "TREASURE" -> {
                int gold = service.collectTreasure();
                appendLog("Found a treasure chest! +" + gold + " gold!");
                refresh();
            }
            case "REST" -> {
                int hp = service.restInRoom();
                appendLog("Empty room — rested and recovered " + hp + " HP.");
                refresh();
            }
        }
        if (service.isGameWon()) {
            JOptionPane.showMessageDialog(app,
                    "Congratulations! You defeated the Dragon and escaped!\n" + service.getCurrentPlayer().getStatusSummary(),
                    "YOU WIN!", JOptionPane.INFORMATION_MESSAGE);
            app.showScreen(MainApp.SCREEN_MENU);
        }
    }

    private void handleUseItem() {
        Player p = service.getCurrentPlayer();
        if (p == null || p.getInventory().isEmpty()) {
            JOptionPane.showMessageDialog(app, "Your inventory is empty."); return;
        }
        List<Item> items = p.getInventory().getItems();
        String[] opts = items.stream().map(Item::toString).toArray(String[]::new);
        int choice = JOptionPane.showOptionDialog(app, "Choose an item to use:",
                "Use Item", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opts, opts[0]);
        if (choice >= 0) {
            String msg = service.useItem(choice);
            appendLog(msg);
            refresh();
        }
    }

    public void refresh() {
        Player p = service.getCurrentPlayer();
        if (p == null) return;
        nameLabel .setText("Name: " + p.getName());
        classLabel.setText("Class: " + p.getPlayerClass());
        levelLabel.setText("Level: " + p.getLevel());
        goldLabel .setText("Gold: " + p.getGold());
        floorLabel.setText("Floor: " + service.getCurrentFloor() + " / 10");
        hpBar.setMaximum(p.getMaxHealth());
        hpBar.setValue(p.getHealth());
        hpBar.setString(p.getHealth() + " / " + p.getMaxHealth());
        double pct = (double) p.getHealth() / p.getMaxHealth();
        hpBar.setForeground(pct > 0.5 ? GUIHelper.HP_GREEN : pct > 0.25 ? Color.ORANGE : GUIHelper.HP_RED);

        // Inventory list
        StringBuilder inv = new StringBuilder();
        List<Item> items = p.getInventory().getItems();
        if (items.isEmpty()) inv.append("[Empty]");
        else items.forEach(i -> inv.append("• ").append(i.getName()).append(" — ").append(i.getDescription()).append("\n"));
        inventoryArea.setText(inv.toString());
    }

    public void appendLog(String msg) {
        eventLog.append(msg + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
    }

    private TitledBorder titledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 100)), title);
        tb.setTitleColor(GUIHelper.TEXT_DIM);
        return tb;
    }
}
