package rpggame.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main menu screen — entry point of the GUI.
 */
public class MainMenuPanel extends JPanel {

    public MainMenuPanel(MainApp app) {
        setLayout(new BorderLayout());
        setBackground(GUIHelper.BG_DARK);

        // Title area
        JPanel titlePanel = new JPanel(new GridLayout(3, 1));
        titlePanel.setBackground(GUIHelper.BG_DARK);
        titlePanel.setBorder(new EmptyBorder(60, 0, 20, 0));
        JLabel title = new JLabel("⚔  DUNGEON QUEST RPG  ⚔", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(GUIHelper.GOLD_COLOR);
        JLabel sub = new JLabel("A Java RPG — COMP603 Project 2", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.ITALIC, 14));
        sub.setForeground(GUIHelper.TEXT_DIM);
        JLabel author = new JLabel("Student: Rohan Sandhu  |  ID: 24252786", SwingConstants.CENTER);
        author.setFont(new Font("Arial", Font.PLAIN, 12));
        author.setForeground(GUIHelper.TEXT_DIM);
        titlePanel.add(title);
        titlePanel.add(sub);
        titlePanel.add(author);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(4, 1, 0, 14));
        btnPanel.setBackground(GUIHelper.BG_DARK);
        btnPanel.setBorder(new EmptyBorder(30, 200, 60, 200));

        JButton newGame  = GUIHelper.makeButton("New Game",   new Color(50, 120, 80));
        JButton loadGame = GUIHelper.makeButton("Load Save",  new Color(60, 90, 140));
        JButton viewLog  = GUIHelper.makeButton("Battle Log", new Color(80, 60, 120));
        JButton quit     = GUIHelper.makeButton("Quit",       new Color(120, 50, 50));

        newGame .addActionListener(e -> app.showScreen(MainApp.SCREEN_NEWGAME));
        loadGame.addActionListener(e -> app.showScreen(MainApp.SCREEN_LOAD));
        viewLog .addActionListener(e -> app.showScreen(MainApp.SCREEN_LOG));
        quit    .addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(app, "Quit the game?", "Quit", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) System.exit(0);
        });

        btnPanel.add(newGame);
        btnPanel.add(loadGame);
        btnPanel.add(viewLog);
        btnPanel.add(quit);

        add(titlePanel, BorderLayout.NORTH);
        add(btnPanel,   BorderLayout.CENTER);
    }
}
