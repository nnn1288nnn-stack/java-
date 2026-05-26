package enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.*;
import map.Map;
import util.Node;

public class Enemy {
    private int x, y;
    private int gridX, gridY;
    private int size = 28;
    private int moveDelay = 20;
    private int delayCounter = 0;
    public Enemy(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.x = gridX * Map.TILE_SIZE + (Map.TILE_SIZE - size) / 2;
        this.y = gridY * Map.TILE_SIZE + (Map.TILE_SIZE - size) / 2;
    }
    public List<Node> findPathBFS(Map map, int targetGridX, int targetGridY) {
        boolean[][] visited = new boolean[Map.COLS][Map.ROWS];
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(this.gridX, this.gridY));
        visited[this.gridX][this.gridY] = true;
        Node targetNode = null;
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            if (curr.x == targetGridX && curr.y == targetGridY) {
                targetNode = curr;
                break;
            }
            for (int i = 0; i < 4; i++) {
                int nextX = curr.x + dx[i];
                int nextY = curr.y + dy[i];
                if (nextX >= 0 && nextX < Map.COLS && nextY >= 0 && nextY < Map.ROWS) {
                    if (!visited[nextX][nextY] && !map.isObstacle(nextX, nextY)) {
                        visited[nextX][nextY] = true;
                        queue.add(new Node(nextX, nextY, curr));
                    }
                }
            }
        }
        List<Node> path = new ArrayList<>();
        if (targetNode != null) {
            Node temp = targetNode;
            while (temp != null) {
                path.add(0, temp);
                temp = temp.parent;
            }
        }
        return path;
    }
    public void update(Map map, int playerGridX, int playerGridY) {
        delayCounter++;
        if (delayCounter >= moveDelay) {
            delayCounter = 0;
            List<Node> path = findPathBFS(map, playerGridX, playerGridY);
            if (path.size() > 1) {
                Node nextStep = path.get(1);
                this.gridX = nextStep.x;
                this.gridY = nextStep.y;
            }
        }
        int targetX = gridX * Map.TILE_SIZE + (Map.TILE_SIZE - size) / 2;
        int targetY = gridY * Map.TILE_SIZE + (Map.TILE_SIZE - size) / 2;
        if (x < targetX) x += 2;
        if (x > targetX) x -= 2;
        if (y < targetY) y += 2;
        if (y > targetY) y -= 2;
    }
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        int[] xPoints = {x + size / 2, x, x + size};
        int[] yPoints = {y + size, y, y};
        g.fillPolygon(xPoints, yPoints, 3);
    }
    public Rectangle getBounds() { return new Rectangle(x, y, size, size); }
}
