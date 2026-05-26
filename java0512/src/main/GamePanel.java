package main;

import bullet.Bullet;
import enemy.Enemy;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import map.Map;
import player.Player;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Map gameMap;
    private List<Bullet> bullets;
    private List<Enemy> enemies;
    private int score = 0, level = 1, enemySpawnTimer = 0;
    private boolean isPaused = false, isGameOver = false;
    private boolean keyUp, keyDown, keyLeft, keyRight;
    public GamePanel() {
        setFocusable(true);
        setPreferredSize(new Dimension(Map.COLS * Map.TILE_SIZE, Map.ROWS * Map.TILE_SIZE));
        setBackground(Color.BLACK);
        addKeyListener(this);
        initGame();
        timer = new Timer(16, this);
        timer.start();
    }
    private void initGame() {
        player = new Player(400, 500);
        gameMap = new Map();
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        score = 0; level = 1; isGameOver = false; isPaused = false;
        spawnEnemy();
    }
    private void spawnEnemy() {
        Random rand = new Random();
        int rx, ry;
        do {
            rx = rand.nextInt(Map.COLS);
            ry = rand.nextInt(4);
        } while (gameMap.isObstacle(rx, ry));
        enemies.add(new Enemy(rx, ry));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) updateGame();
        repaint();
    }
    private void updateGame() {
        int dx = 0, dy = 0;
        if (keyUp) dy = -1;
        if (keyDown) dy = 1;
        if (keyLeft) dx = -1;
        if (keyRight) dx = 1;
        player.move(dx, dy);
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            b.update();
            if (!b.isActive()) bulletIt.remove();
        }
        enemySpawnTimer++;
        if (enemySpawnTimer > 120) { spawnEnemy(); enemySpawnTimer = 0; }
        for (Enemy enemy : enemies) enemy.update(gameMap, player.getGridX(), player.getGridY());
        checkCollisions();
    }
    private void checkCollisions() {
        for (Bullet b : bullets) {
            for (Enemy enemy : enemies) {
                if (b.getBounds().intersects(enemy.getBounds())) {
                    b.setInactive();
                    enemies.remove(enemy);
                    score += 100;
                    if (score % 500 == 0) level++;
                    break;
                }
            }
        }
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            if (enemy.getBounds().intersects(player.getBounds())) {
                player.decreaseHp();
                enemyIt.remove();
                if (player.getHp() <= 0) isGameOver = true;
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.draw(g);
        player.draw(g);
        for (Bullet b : bullets) b.draw(g);
        for (Enemy e : enemies) e.draw(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("SCORE: " + score, 20, 25);
        g.drawString("HP: " + player.getHp(), 200, 25);
        g.drawString("LEVEL: " + level, 350, 25);
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PAUSED", getWidth() / 2 - 70, getHeight() / 2);
        }
        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", getWidth() / 2 - 110, getHeight() / 2 - 20);
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) keyUp = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) keyDown = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) keyLeft = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) keyRight = true;
        if (key == KeyEvent.VK_SPACE && !isGameOver && !isPaused) {
            bullets.add(new Bullet(player.getX() + 15, player.getY()));
        }
        if (key == KeyEvent.VK_P) isPaused = !isPaused;
        if (key == KeyEvent.VK_R) initGame();
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) keyUp = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) keyDown = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) keyLeft = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) keyRight = false;
    }
    @Override public void keyTyped(KeyEvent e) {}
}
