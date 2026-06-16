package farmgame;

import java.awt.*;

/**
 * 遊戲地圖 — 二維網格、碰撞判斷、耕作狀態更新、渲染
 */
public class GameMap {

    public static final int TILE_SIZE = 32;
    public static final int COLS      = 25;  // 800 / 32
    public static final int ROWS      = 19;  // 600 / 32（保留一些邊距）

    private final Tile[][] tiles;

    public GameMap() {
        tiles = new Tile[ROWS][COLS];
        // 邊界放 WALL 圍牆，內部放 GRASS 草地
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) {
                    tiles[r][c] = new Tile(Tile.TileType.WALL);
                } else {
                    tiles[r][c] = new Tile(Tile.TileType.GRASS);
                }
            }
        }
    }

    /** 檢查某格是否可通行 */
    public boolean isWalkable(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return false;
        return tiles[row][col].type != Tile.TileType.WALL;
    }

    /** 安全取得某格（越界回傳 null） */
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return null;
        return tiles[row][col];
    }

    // ============================================================
    //  成長更新
    // ============================================================

    /** 每次幀更新時呼叫，累計時間推動作物成長 */
    public void updateGrowth(long deltaMs) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Tile t = tiles[r][c];
                if (t.type == Tile.TileType.WALL) continue;

                // 已澆水 + 有播種 → 等待進入成長階段
                if (t.farmState == Tile.FarmState.WATERED && t.cropType.length() > 0) {
                    t.growthTimerMs += deltaMs;
                    if (t.growthTimerMs >= Tile.TIME_WATERED_TO_GROWING) {
                        t.farmState = Tile.FarmState.GROWING;
                        t.growthStage = 1;
                        t.growthTimerMs = 0;
                    }
                }
                // 成長中 → 推進階段，直到成熟
                else if (t.farmState == Tile.FarmState.GROWING) {
                    t.growthTimerMs += deltaMs;
                    if (t.growthTimerMs >= Tile.TIME_PER_GROWTH_STAGE) {
                        t.growthStage++;
                        t.growthTimerMs = 0;
                        if (t.growthStage >= Tile.TOTAL_GROWTH_STAGES) {
                            t.farmState = Tile.FarmState.MATURE;
                        }
                    }
                }
            }
        }
    }

    // ============================================================
    //  渲染
    // ============================================================

    public void render(Graphics g) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Tile t = tiles[r][c];
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;

                // ─ 基底地形 ─
                if (t.type == Tile.TileType.WALL) {
                    g.setColor(new Color(100, 90, 80));
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                } else if (t.farmState == Tile.FarmState.NONE) {
                    // 一般草地 — 隨機一點的綠色
                    int green = 180 + ((r * 7 + c * 13) % 40);
                    g.setColor(new Color(100, green, 60));
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }

                // ─ 農場狀態疊層 ─
                if (t.farmState != Tile.FarmState.NONE && t.type != Tile.TileType.WALL) {
                    drawFarmTile(g, t, x, y);
                }

                // ─ 極淡網格線 ─
                g.setColor(new Color(0, 0, 0, 12));
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /** 繪製農場狀態（犁土、作物等） */
    private void drawFarmTile(Graphics g, Tile t, int x, int y) {
        switch (t.farmState) {
            case TILLED -> {
                g.setColor(new Color(139, 90, 43));
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                // 犁溝紋理
                g.setColor(new Color(120, 75, 35));
                for (int i = 0; i < 3; i++) {
                    g.fillRect(x + 4 + i * 10, y + 6, 4, TILE_SIZE - 12);
                }
            }
            case PLANTED -> {
                g.setColor(new Color(120, 80, 40));
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                // 小小種子記號
                g.setColor(new Color(80, 60, 30));
                g.fillOval(x + 12, y + 12, 8, 8);
            }
            case WATERED -> {
                g.setColor(new Color(70, 65, 85));
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                // 水光
                g.setColor(new Color(100, 120, 180, 60));
                g.fillRect(x + 4, y + 4, TILE_SIZE - 8, 4);
            }
            case GROWING -> {
                g.setColor(new Color(139, 90, 43));
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                drawCrop(g, x, y, t.growthStage);
            }
            case MATURE -> {
                g.setColor(new Color(139, 90, 43));
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                drawCrop(g, x, y, 5); // 成熟用 5 號階段
            }
        }
    }

    /** 依階段繪製作物外觀 */
    private void drawCrop(Graphics g, int x, int y, int stage) {
        int cx    = x + TILE_SIZE / 2;
        int baseY = y + TILE_SIZE - 6;

        g.setColor(new Color(50, 180, 50));

        switch (stage) {
            case 1 -> {
                g.fillRect(cx - 1, baseY - 5, 2, 5);
                g.fillOval(cx - 3, baseY - 7, 6, 4);
            }
            case 2 -> {
                g.fillRect(cx - 2, baseY - 10, 4, 10);
                g.fillOval(cx - 5, baseY - 12, 10, 6);
                g.fillOval(cx - 3, baseY - 6,  6, 4);
            }
            case 3 -> {
                g.fillRect(cx - 3, baseY - 14, 6, 14);
                g.fillOval(cx - 7, baseY - 16, 14, 8);
                g.fillOval(cx - 5, baseY - 10, 10, 6);
                g.fillOval(cx - 4, baseY - 4,   8, 5);
            }
            case 4 -> {
                g.fillRect(cx - 3, baseY - 16, 6, 16);
                g.fillOval(cx - 8, baseY - 18, 16, 10);
                g.fillOval(cx - 6, baseY - 12, 12, 8);
                g.fillOval(cx - 5, baseY - 6,  10, 6);
            }
            case 5 -> {
                g.fillRect(cx - 3, baseY - 16, 6, 16);
                g.fillOval(cx - 8, baseY - 18, 16, 10);
                g.fillOval(cx - 6, baseY - 12, 12, 8);
                g.fillOval(cx - 5, baseY - 6,  10, 6);
                // 果實
                g.setColor(new Color(255, 210, 50));
                g.fillOval(cx - 5, baseY - 21, 10, 10);
                // 亮點
                g.setColor(new Color(255, 240, 150));
                g.fillOval(cx - 3, baseY - 19, 3, 3);
            }
        }
    }
}
