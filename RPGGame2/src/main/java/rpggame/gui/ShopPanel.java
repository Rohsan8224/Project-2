package rpggame.gui;

import rpggame.data.ShopData;
import rpggame.model.Item;
import rpggame.model.Player;
import rpggame.service.GameService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Shop screen — browse and purchase items.
 */
public class ShopPanel extends JPanel {

    private final GameService service;
    private final MainApp app;
    private final JLabel goldLabel = GUIHelper.makeLabel("Gold: 0");
    private final DefaultListModel<Item> shopModel = new DefaultListModel<>();
    private final JList<Item> shopList = new JList<>(shopModel);
    private final JTextArea descArea = GUIHelper.makeLogArea();

    public ShopPanel(MainApp app, GameService service) {
        this.app = app;
        this.service = service;
        setLayout(new BorderLayout(8, 8));
        setBackground(GUIHelper.BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(GUIHelper.BG_DARK);
        header.add(GUIHelper.makeTitle("Shop"), BorderLayout.WEST);
        goldLabel.setFont(new Font("Arial", Font.BOLD, 18));
        goldLabel.setForeground(GUIHelper.GOLD_COLOR);
        goldLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        header.add(goldLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Shop list
        shopList.setBackground(new Color(25, 25, 40));
        shopList.setForeground(Color.WHITE);
        shopList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        shopList.setSelectionBackground(new Color(80, 110, 50));
        shopList.setFixedCellHeight(36);
        shopList.addListSelectionListener(e -> {
            Item sel = shopList.getSelectedValue();
            if (sel != null) descArea.setText(sel.getName() + "\n\n" + sel.getDescription() + "\n\nCost: " + sel.getCost() + " gold");
        });

        JScrollPane listScroll = new JScrollPane(shopList);
        listScroll.setPreferredSize(new Dimension(450, 0));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, descScroll);
        split.setDividerLocation(460);
        split.setBackground(GUIHelper.BG_DARK);
        add(split, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        btnRow.setBackground(GUIHelper.BG_DARK);
        JButton buy  = GUIHelper.makeButton("Buy Selected", new Color(80, 130, 50));
        JButton back = GUIHelper.makeButton("Leave Shop");

        buy.addActionListener(e -> {
            Item sel = shopList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(app, "Select an item first."); return; }
            boolean ok = service.buyItem(sel);
            if (ok) {
                JOptionPane.showMessageDialog(app, "Purchased " + sel.getName() + "!");
                refresh();
            } else {
                Player p = service.getCurrentPlayer();
                if (p.getGold() < sel.getCost())
                    JOptionPane.showMessageDialog(app, "Not enough gold!");
                else
                    JOptionPane.showMessageDialog(app, "Inventory is full!");
            }
        });
        back.addActionListener(e -> app.showScreen(MainApp.SCREEN_DUNGEON));

        btnRow.add(back);
        btnRow.add(buy);
        add(btnRow, BorderLayout.SOUTH);
    }

    public void refresh() {
        Player p = service.getCurrentPlayer();
        if (p != null) goldLabel.setText("Gold: " + p.getGold());
        shopModel.clear();
        List<Item> items = ShopData.getShopItems();
        items.forEach(shopModel::addElement);
    }
}
