package farmgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 遊戲主面板 — 遊戲迴圈、按鍵輸入、邏輯更新、畫面繪製
 */
public class GamePanel extends JPanel implements Runnable {

    // ---- 遊戲迴圈 ----
    private Thread gameThread;
    private volatile boolean running = false;
    private static final int TARGET_FPS = 60;
    private static final long FRAME_MS = 1000 / TARGET_FPS;

    // ---- 遊戲物件 ----
    private GameMap      gameMap;
    private Player       player;
    private Inventory    inventory;
    private ShippingBin  shippingBin;

    // ---- 按鍵狀態 ----
    // 連續按壓（移動）
    private boolean upHeld, downHeld, leftHeld, rightHeld;
    // 單次動作（工具／互動），用邊緣觸發避免一幀內重複觸發
    private boolean actionPressed,  actionPrev;
    private boolean seedPressed,    seedPrev;
    private boolean waterPressed,   waterPrev;
    private boolean interactPressed, interactPrev;

    // ---- 移動冷卻 ----
    private long lastMoveTime = 0;
    private static final long MOVE_INTERVAL_MS = 130; // 每 130ms 可移動一格

    // ---- 訊息提示 ----
    private String toastText   = "";
    private long   toastEnd    = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        gameMap     = new GameMap();
        player      = new Player(gameMap);
        inventory   = new Inventory();
        shippingBin = new ShippingBin(GameMap.COLS - 3, GameMap.ROWS - 3);

        setupInput();
        startGameLoop();
    }

    // ============================================================
    //  輸入處理
    // ============================================================

    private void setupInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> { upHeld = true;   player.direction = 0; }
                    case KeyEvent.VK_S -> { downHeld = true; player.direction = 1; }
                    case KeyEvent.VK_A -> { leftHeld = true; player.direction = 2; }
                    case KeyEvent.VK_D -> { rightHeld = true;player.direction = 3; }
                    case KeyEvent.VK_J -> actionPressed   = true;
                    case KeyEvent.VK_K -> seedPressed     = true;
                    case KeyEvent.VK_L -> waterPressed    = true;
                    case KeyEvent.VK_E -> interactPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> upHeld    = false;
                    case KeyEvent.VK_S -> downHeld  = false;
                    case KeyEvent.VK_A -> leftHeld  = false;
                    case KeyEvent.VK_D -> rightHeld = false;
                    case KeyEvent.VK_J -> actionPressed   = false;
                    case KeyEvent.VK_K -> seedPressed     = false;
                    case KeyEvent.VK_L -> waterPressed    = false;
                    case KeyEvent.VK_E -> interactPressed = false;
                }
            }
        });
    }

    // ============================================================
    //  遊戲迴圈
    // ============================================================

    private void startGameLoop() {
        running = true;
        gameThread = new Thread(this, "GameLoop");
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (running) {
            long now   = System.nanoTime();
            long delta = (now - lastTime) / 1_000_000; // ms
            lastTime   = now;

            if (delta > 200) delta = 200; // 防止突然卡頓造成跳幀

            update(delta);
            repaint();

            long elapsed = (System.nanoTime() - now) / 1_000_000;
            long sleep   = FRAME_MS - elapsed;
            if (sleep > 0) {
                try { Thread.sleep(sleep); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ============================================================
    //  邏輯更新
    // ============================================================

    private void update(long deltaMs) {
        // ─ 移動（含冷卻） ─
        long now = System.currentTimeMillis();
        if (now - lastMoveTime >= MOVE_INTERVAL_MS) {
            int dRow = 0, dCol = 0;
            if (upHeld)    { dRow = -1; player.direction = 0; }
            else if (downHeld)  { dRow = 1;  player.direction = 1; }
            else if (leftHeld)  { dCol = -1; player.direction = 2; }
            else if (rightHeld) { dCol = 1;  player.direction = 3; }

            if (dRow != 0 || dCol != 0) {
                player.move(dRow, dCol);
                lastMoveTime = now;
            }
        }

        // ─ 工具／互動（邊緣觸發） ─
        if (actionPressed   && !actionPrev)   useAction();
        if (seedPressed     && !seedPrev)     useSeeds();
        if (waterPressed    && !waterPrev)    useWateringCan();
        if (interactPressed && !interactPrev) interact();

        actionPrev   = actionPressed;
        seedPrev     = seedPressed;
        waterPrev    = waterPressed;
        interactPrev = interactPressed;

        // ─ 作物成長 ─
        gameMap.updateGrowth(deltaMs);
    }

    // ============================================================
    //  工具與互動邏輯
    // ============================================================

    /** 取得玩家面向的前一格座標 */
    private int[] facingTile() {
        int r = player.gridRow;
        int c = player.gridCol;
        switch (player.direction) {
            case 0 -> r--;  // 上
            case 1 -> r++;  // 下
            case 2 -> c--;  // 左
            case 3 -> c++;  // 右
        }
        return new int[]{r, c};
    }

    /** J 鍵 — 鋤頭／收成 */
    private void useAction() {
        int[] t = facingTile();
        Tile tile = gameMap.getTile(t[0], t[1]);
        if (tile == null || tile.type == Tile.TileType.WALL) {
            showToast("那裡不能操作");
            return;
        }

        if (tile.farmState == Tile.FarmState.MATURE) {
            inventory.harvestCrop();
            tile.farmState    = Tile.FarmState.TILLED;
            tile.cropType     = "";
            tile.growthStage  = 0;
            tile.growthTimerMs = 0;
            showToast("收成了 1 個作物！");
        } else if (tile.farmState == Tile.FarmState.NONE) {
            tile.farmState = Tile.FarmState.TILLED;
            showToast("翻土完成");
        } else {
            showToast("這個狀態不能鋤地");
        }
    }

    /** K 鍵 — 播種 */
    private void useSeeds() {
        int[] t = facingTile();
        Tile tile = gameMap.getTile(t[0], t[1]);
        if (tile == null) return;

        if (tile.farmState == Tile.FarmState.TILLED) {
            tile.farmState = Tile.FarmState.PLANTED;
            tile.cropType  = "防風草";
            showToast("種下了防風草！");
        } else {
            showToast("請先翻土再播種");
        }
    }

    /** L 鍵 — 澆水 */
    private void useWateringCan() {
        int[] t = facingTile();
        Tile tile = gameMap.getTile(t[0], t[1]);
        if (tile == null) return;

        if (tile.farmState == Tile.FarmState.PLANTED || tile.farmState == Tile.FarmState.TILLED) {
            tile.farmState   = Tile.FarmState.WATERED;
            tile.growthTimerMs = 0;
            showToast("澆水完成！");
        } else {
            showToast("這裡不需要澆水");
        }
    }

    /** E 鍵 — 出貨箱互動 */
    private void interact() {
        int dr = Math.abs(player.gridRow - shippingBin.row);
        int dc = Math.abs(player.gridCol - shippingBin.col);
        if (dr + dc <= 1) {
            int sold = inventory.sellAll();
            if (sold > 0) {
                showToast("賣出 " + sold + " 個作物，獲得 " + (sold * 10) + " 金幣！");
            } else {
                showToast("背包裡沒有作物可以賣");
            }
        } else {
            showToast("請靠近出貨箱再按 E");
        }
    }

    /** 短暫提示訊息 */
    private void showToast(String msg) {
        toastText = msg;
        toastEnd  = System.currentTimeMillis() + 2500; // 顯示 2.5 秒
    }

    // ============================================================
    //  繪製
    // ============================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. 地圖底層
        gameMap.render(g);
        // 2. 出貨箱
        shippingBin.render(g);
        // 3. 玩家
        player.render(g);
        // 4. UI 疊層
        renderUI(g);
    }

    /** 繪製文字 UI（最上層，半透明背景） */
    private void renderUI(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font baseFont = new Font("微軟正黑體", Font.BOLD, 15);

        // ─ 頂部資訊列 ─
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, 800, 32);

        g.setFont(baseFont);
        g.setColor(Color.WHITE);
        g.drawString("Gold: "  + inventory.gold       + " G",    10, 22);
        g.drawString("背包: "  + inventory.cropCount   + " 個",  140, 22);
        g.drawString("J=鋤頭/收成  K=播種  L=澆水  E=出貨",        310, 22);
        g.drawString("WASD=移動",                                670, 22);

        // ─ 提示訊息 ─
        if (System.currentTimeMillis() < toastEnd) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(250, 260, 300, 36, 12, 12);
            g.setColor(new Color(255, 255, 200));
            g.setFont(new Font("微軟正黑體", Font.BOLD, 18));
            g.drawString(toastText, 400 - g.getFontMetrics().stringWidth(toastText) / 2, 285);
        }
    }
}
