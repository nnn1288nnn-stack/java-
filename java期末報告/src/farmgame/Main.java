package farmgame;

import javax.swing.*;

/**
 * 遊戲入口 — 建立 JFrame 視窗並啟動遊戲
 */
public class Main {
    public static void main(String[] args) {
        // 設定系統外觀
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("農場物語 - 簡易農場遊戲");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
