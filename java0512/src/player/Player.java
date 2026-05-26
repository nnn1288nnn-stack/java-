package player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import map.Map;

public class Player {
    private int x, y;
    private int speed = 5;
    private int width = 30;
    private int height = 30;
    private int hp = 3;
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void move(int dx, int dy) {
        int nextX = x + dx * speed;
        int nextY = y + dy * speed;
        int gridX = nextX / Map.TILE_SIZE;
        int gridY = nextY / Map.TILE_SIZE;
        if (gridX >= 0 && gridX < Map.COLS && gridY >= 0 && gridY < Map.ROWS) {
            x = Math.max(0, Math.min(nextX, Map.COLS * Map.TILE_SIZE - width));
            y = Math.max(0, Math.min(nextY, Map.ROWS * Map.TILE_SIZE - height));
        }
    }
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        int[] xPoints = {x + width / 2, x, x + width};
        int[] yPoints = {y, y + height, y + height};
        g.fillPolygon(xPoints, yPoints, 3);
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getGridX() { return (x + width / 2) / Map.TILE_SIZE; }
    public int getGridY() { return (y + height / 2) / Map.TILE_SIZE; }
    public int getHp() { return hp; }
    public void decreaseHp() { hp--; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}
