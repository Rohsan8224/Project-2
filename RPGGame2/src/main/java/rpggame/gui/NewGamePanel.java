package rpggame.gui;

import rpggame.model.Player;
import rpggame.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Screen for creating a new character.
 */
public class NewGamePanel extends JPanel {

    private final JTextField nameField = new JTextField(20);
    private final JComboBox<Player.PlayerClass> classBox =
            new JComboBox<>(Player.PlayerClass.values());

    public NewGamePanel(MainApp app, GameService service) {
        setLayout(new BorderLayout());
        setBackground(GUIHelper.BG_DARK);

        add(GUIHelper.makeTitle("Create Your Character"), BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(GUIHelper.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Name row
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(GUIHelper.makeLabel("Character Name:"), gbc);
        gbc.gridx = 1;
        nameField.setBackground(new Color(45, 45, 70));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        form.add(nameField, gbc);

        // Class row
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(GUIHelper.makeLabel("Choose Class:"), gbc);
        gbc.gridx = 1;
        classBox.setBackground(new Color(45, 45, 70));
        classBox.setForeground(Color.WHITE);
        form.add(classBox, gbc);

        // Class description
        JTextArea desc = GUIHelper.makeLogArea();
        desc.setText(getClassDesc(Player.PlayerClass.WARRIOR));
        classBox.addActionListener(e ->
                desc.setText(getClassDesc((Player.PlayerClass) classBox.getSelectedItem())));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(desc), gbc);

        add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnRow.setBackground(GUIHelper.BG_DARK);
        JButton start = GUIHelper.makeButton("Start Adventure", new Color(50, 130, 80));
        JButton back  = GUIHelper.makeButton("Back");

        start.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(app, "Please enter a name!"); return; }
            Player.PlayerClass pc = (Player.PlayerClass) classBox.getSelectedItem();
            service.createPlayer(name, pc);
            app.showScreen(MainApp.SCREEN_DUNGEON);
        });
        back.addActionListener(e -> app.showScreen(MainApp.SCREEN_MENU));

        btnRow.add(back);
        btnRow.add(start);
        add(btnRow, BorderLayout.SOUTH);
    }

    private String getClassDesc(Player.PlayerClass pc) {
        return switch (pc) {
            case WARRIOR -> "WARRIOR\n\nHP: 120  |  ATK: 18  |  DEF: 10\n\nA battle-hardened fighter. " +
                    "High health and defence make Warriors tough to kill.\n\n" +
                    "Special Ability — Shield Bash: Double-damage strike that also adds defence bonus. 3-turn cooldown.";
            case MAGE    -> "MAGE\n\nHP: 80   |  ATK: 25  |  DEF: 3\n\nA glass-cannon spellcaster. " +
                    "Devastating attack power but fragile defences.\n\n" +
                    "Special Ability — Fireball: Triple-damage magic blast. 3-turn cooldown.";
            case ROGUE   -> "ROGUE\n\nHP: 100  |  ATK: 22  |  DEF: 6\n\nA nimble assassin with balanced stats. " +
                    "Good all-rounder with high critical hit potential.\n\n" +
                    "Special Ability — Backstab: 2.5x critical damage strike. 3-turn cooldown.";
        };
    }
}
