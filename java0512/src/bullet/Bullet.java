package bullet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet {
    private int x, y;
    private int speed = 10;
    private int radius = 6;
    private boolean active = true;
    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void update() {
        y -= speed;
        if (y < 0) active = false;
    }
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }
    public boolean isActive() { return active; }
    public void setInactive() { this.active = false; }
    public Rectangle getBounds() {
        return new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
    }
}
