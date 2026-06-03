package rpggame.gui;

import rpggame.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Displays the player's combat history from the database.
 */
public class BattleLogPanel extends JPanel {

    private final GameService service;
    private final MainApp app;
    private final JTextArea logArea = GUIHelper.makeLogArea();

    public BattleLogPanel(MainApp app, GameService service) {
        this.app = app;
        this.service = service;
        setLayout(new BorderLayout(8, 8));
        setBackground(GUIHelper.BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(GUIHelper.makeTitle("Battle Log"), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        btnRow.setBackground(GUIHelper.BG_DARK);
        JButton back = GUIHelper.makeButton("Back");
        back.addActionListener(e -> app.showScreen(MainApp.SCREEN_MENU));
        btnRow.add(back);
        add(btnRow, BorderLayout.SOUTH);
    }

    public void refresh() {
        List<String> entries = service.getBattleLog();
        if (entries.isEmpty()) {
            logArea.setText("No battles recorded yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-8s %-20s %-8s %-8s %-8s %-16s%n",
                    "Floor", "Enemy", "Outcome", "Gold", "XP", "Time"));
            sb.append("-".repeat(72)).append("\n");
            entries.forEach(e -> sb.append(e).append("\n"));
            logArea.setText(sb.toString());
            logArea.setCaretPosition(0);
        }
    }
}
