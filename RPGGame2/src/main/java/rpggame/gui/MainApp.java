package rpggame.gui;

import rpggame.db.DatabaseManager;
import rpggame.service.GameService;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point.
 * Creates the root JFrame and manages screen navigation via CardLayout.
 * Design Pattern: MVC (this is the application shell / View coordinator)
 */
public class MainApp extends JFrame {

    public static final String SCREEN_MENU    = "MENU";
    public static final String SCREEN_NEWGAME = "NEWGAME";
    public static final String SCREEN_LOAD    = "LOAD";
    public static final String SCREEN_DUNGEON = "DUNGEON";
    public static final String SCREEN_COMBAT  = "COMBAT";
    public static final String SCREEN_SHOP    = "SHOP";
    public static final String SCREEN_LOG     = "LOG";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final GameService service = new GameService();

    // Panels (lazily initialised on navigate)
    private MainMenuPanel menuPanel;
    private NewGamePanel  newGamePanel;
    private LoadGamePanel loadGamePanel;
    private DungeonPanel  dungeonPanel;
    private CombatPanel   combatPanel;
    private ShopPanel     shopPanel;
    private BattleLogPanel logPanel;

    public MainApp() {
        setTitle("Dungeon Quest RPG");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                service.saveCurrentPlayer();
                DatabaseManager.getInstance().shutdown();
                dispose();
                System.exit(0);
            }
        });
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        applyTheme();
        buildPanels();

        add(cardPanel);
        showScreen(SCREEN_MENU);
        setVisible(true);
    }

    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Panel.background",    new Color(30, 30, 45));
        UIManager.put("Label.foreground",    Color.WHITE);
        UIManager.put("Button.background",   new Color(60, 60, 90));
        UIManager.put("Button.foreground",   Color.WHITE);
        UIManager.put("TextArea.background", new Color(20, 20, 35));
        UIManager.put("TextArea.foreground", new Color(200, 230, 200));
    }

    private void buildPanels() {
        menuPanel    = new MainMenuPanel(this);
        newGamePanel = new NewGamePanel(this, service);
        loadGamePanel= new LoadGamePanel(this, service);
        dungeonPanel = new DungeonPanel(this, service);
        combatPanel  = new CombatPanel(this, service);
        shopPanel    = new ShopPanel(this, service);
        logPanel     = new BattleLogPanel(this, service);

        cardPanel.add(menuPanel,    SCREEN_MENU);
        cardPanel.add(newGamePanel, SCREEN_NEWGAME);
        cardPanel.add(loadGamePanel,SCREEN_LOAD);
        cardPanel.add(dungeonPanel, SCREEN_DUNGEON);
        cardPanel.add(combatPanel,  SCREEN_COMBAT);
        cardPanel.add(shopPanel,    SCREEN_SHOP);
        cardPanel.add(logPanel,     SCREEN_LOG);
    }

    /** Switches to a named screen and refreshes it. */
    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
        // Refresh panels that need current state
        switch (name) {
            case SCREEN_DUNGEON -> dungeonPanel.refresh();
            case SCREEN_COMBAT  -> combatPanel.refresh();
            case SCREEN_SHOP    -> shopPanel.refresh();
            case SCREEN_LOAD    -> loadGamePanel.refresh();
            case SCREEN_LOG     -> logPanel.refresh();
        }
    }

    public static void main(String[] args) {
        // Ensure Derby boots on the main thread before any GUI
        DatabaseManager.getInstance();
        SwingUtilities.invokeLater(MainApp::new);
    }
}
