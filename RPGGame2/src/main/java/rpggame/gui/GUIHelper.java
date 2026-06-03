package rpggame.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Shared Swing UI factory methods for consistent styling across all panels.
 */
public class GUIHelper {

    public static final Color BG_DARK    = new Color(25, 25, 40);
    public static final Color BG_PANEL   = new Color(35, 35, 55);
    public static final Color BG_CARD    = new Color(45, 45, 70);
    public static final Color ACCENT     = new Color(100, 160, 255);
    public static final Color GOLD_COLOR = new Color(255, 210, 60);
    public static final Color HP_GREEN   = new Color(60, 200, 100);
    public static final Color HP_RED     = new Color(220, 60, 60);
    public static final Color TEXT_MAIN  = new Color(220, 220, 235);
    public static final Color TEXT_DIM   = new Color(150, 150, 170);

    public static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                new EmptyBorder(8, 18, 8, 18)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton makeButton(String text) {
        return makeButton(text, BG_CARD);
    }

    public static JLabel makeTitle(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 28));
        lbl.setForeground(ACCENT);
        lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
        return lbl;
    }

    public static JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    public static JLabel makeSmallLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DIM);
        return lbl;
    }

    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        return p;
    }

    public static JProgressBar makeHPBar(int value, int max) {
        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(value + " / " + max);
        bar.setFont(new Font("Arial", Font.BOLD, 11));
        bar.setPreferredSize(new Dimension(200, 22));
        double pct = (double) value / max;
        bar.setForeground(pct > 0.5 ? HP_GREEN : pct > 0.25 ? Color.ORANGE : HP_RED);
        bar.setBackground(new Color(40, 40, 60));
        return bar;
    }

    public static JTextArea makeLogArea() {
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setBackground(new Color(18, 18, 30));
        ta.setForeground(new Color(190, 230, 190));
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        return ta;
    }

    public static JPanel setBackground(JPanel p, Color c) {
        p.setBackground(c);
        return p;
    }
}
