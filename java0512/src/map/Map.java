package map;

import java.awt.Color;
import java.awt.Graphics;

public class Map {
    public static final int COLS = 20;
    public static final int ROWS = 15;
    public static final int TILE_SIZE = 40;
    private int[][] grid;
    public Map() {
        grid = new int[COLS][ROWS];
        generateObstacles();
    }
    private void generateObstacles() {
        grid[5][5] = 1; grid[5][6] = 1; grid[5][7] = 1;
        grid[14][3] = 1; grid[14][4] = 1;
        grid[10][10] = 1; grid[11][10] = 1; grid[12][10] = 1;
        grid[3][11] = 1; grid[4][11] = 1;
    }
    public boolean isObstacle(int x, int y) {
        if (x < 0 || x >= COLS || y < 0 || y >= ROWS) return true;
        return grid[x][y] == 1;
    }
    public void draw(Graphics g) {
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
}
