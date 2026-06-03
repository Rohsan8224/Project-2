package rpggame.gui;

import rpggame.dao.PlayerDAO.SavedPlayerRecord;
import rpggame.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Screen showing all saved players — load or delete them.
 */
public class LoadGamePanel extends JPanel {

    private final GameService service;
    private final MainApp app;
    private final DefaultListModel<SavedPlayerRecord> listModel = new DefaultListModel<>();
    private final JList<SavedPlayerRecord> saveList = new JList<>(listModel);

    public LoadGamePanel(MainApp app, GameService service) {
        this.app = app;
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(GUIHelper.BG_DARK);

        add(GUIHelper.makeTitle("Load Save"), BorderLayout.NORTH);

        saveList.setBackground(new Color(25, 25, 40));
        saveList.setForeground(Color.WHITE);
        saveList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        saveList.setSelectionBackground(new Color(60, 90, 140));
        saveList.setFixedCellHeight(36);
        JScrollPane scroll = new JScrollPane(saveList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100)));
        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(GUIHelper.BG_DARK);
        centre.setBorder(new EmptyBorder(10, 40, 10, 40));
        centre.add(scroll, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnRow.setBackground(GUIHelper.BG_DARK);
        JButton load   = GUIHelper.makeButton("Load Selected", new Color(50, 130, 80));
        JButton delete = GUIHelper.makeButton("Delete Selected", new Color(140, 50, 50));
        JButton back   = GUIHelper.makeButton("Back");

        load.addActionListener(e -> {
            SavedPlayerRecord sel = saveList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(app, "Select a save first."); return; }
            service.loadPlayer(sel);
            app.showScreen(MainApp.SCREEN_DUNGEON);
        });
        delete.addActionListener(e -> {
            SavedPlayerRecord sel = saveList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(app, "Select a save first."); return; }
            int ok = JOptionPane.showConfirmDialog(app,
                    "Delete save for " + sel.name() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                service.deleteSave(sel.id());
                refresh();
            }
        });
        back.addActionListener(e -> app.showScreen(MainApp.SCREEN_MENU));

        btnRow.add(back);
        btnRow.add(delete);
        btnRow.add(load);
        add(btnRow, BorderLayout.SOUTH);
    }

    public void refresh() {
        listModel.clear();
        List<SavedPlayerRecord> saves = service.getAllSaves();
        for (SavedPlayerRecord r : saves) listModel.addElement(r);
        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(app, "No saved games found.");
        }
    }
}
