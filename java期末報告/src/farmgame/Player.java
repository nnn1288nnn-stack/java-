package farmgame;

import java.awt.*;

/**
 * 玩家角色 — 網格移動、碰撞偵測、繪製
 */
public class Player {

    // 玩家在網格中的座標
    public int gridRow;
    public int gridCol;

    // 面向：0=上  1=下  2=左  3=右
    public int direction = 1;

    private GameMap gameMap;

    public Player(GameMap map) {
        this.gameMap = map;
        // 出生點：地圖正中央
        gridCol = GameMap.COLS / 2;
        gridRow = GameMap.ROWS / 2;
    }

    /** 嘗試移動一格，若目標可通行則移動 */
    public void move(int dRow, int dCol) {
        int tr = gridRow + dRow;
        int tc = gridCol + dCol;
        if (gameMap.isWalkable(tr, tc)) {
            gridRow = tr;
            gridCol = tc;
        }
    }

    /** 繪製玩家 */
    public void render(Graphics g) {
        int s   = GameMap.TILE_SIZE;
        int x   = gridCol * s;
        int y   = gridRow * s;
        int cx  = x + s / 2;
        int cy  = y + s / 2;

        // 身體襯衫
        g.setColor(new Color(50, 120, 220));
        g.fillRoundRect(x + 4, y + 8, s - 8, s - 12, 6, 6);
        // 頭部
        g.setColor(new Color(255, 220, 180));
        g.fillOval(x + 7, y + 2, s - 14, s - 14);
        // 眼睛
        g.setColor(Color.BLACK);
        g.fillOval(x + 10, y + 8, 3, 3);
        g.fillOval(x + 18, y + 8, 3, 3);
        // 面向箭頭
        g.setColor(Color.WHITE);
        switch (direction) {
            case 0 -> g.fillRect(cx - 2, cy - 10, 4, 6);  // 上
            case 1 -> g.fillRect(cx - 2, cy + 4,  4, 6);  // 下
            case 2 -> g.fillRect(cx - 10, cy - 2, 6, 4);  // 左
            case 3 -> g.fillRect(cx + 4,  cy - 2, 6, 4);  // 右
        }

        // 除錯：高亮玩家所在格
        g.setColor(new Color(255, 255, 0, 50));
        g.drawRect(x, y, s, s);
    }
}
