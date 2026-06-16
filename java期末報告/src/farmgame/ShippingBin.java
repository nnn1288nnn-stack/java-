package farmgame;

import java.awt.*;

/**
 * 出貨箱 — 固定在地圖上的物件，玩家靠近按 E 可賣出作物
 */
public class ShippingBin {

    public final int row;
    public final int col;

    public ShippingBin(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public void render(Graphics g) {
        int s = GameMap.TILE_SIZE;
        int x = col * s;
        int y = row * s;

        // 木箱本體
        g.setColor(new Color(139, 69, 19));
        g.fillRect(x + 2, y + 6, s - 4, s - 10);
        // 箱蓋
        g.setColor(new Color(180, 100, 30));
        g.fillRect(x + 1, y + 2, s - 2, 6);
        // 木紋
        g.setColor(new Color(120, 55, 15));
        g.drawLine(x + 4, y + 12, x + s - 4, y + 12);
        g.drawLine(x + 4, y + 18, x + s - 4, y + 18);
        // 標籤
        g.setColor(Color.WHITE);
        g.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        g.drawString("出貨箱", x + 3, y + 22);
    }
}
