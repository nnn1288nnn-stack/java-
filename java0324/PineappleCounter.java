import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class PineappleCounter {

    // ★ 黃金參數調校區
    private static final int MIN_PIXEL_COUNT = 45;   // 取 20 與 80 的中間值
    private static final int MAX_PIXEL_COUNT = 1500; 
    private static final int MIN_DISTANCE_BETWEEN_PINEAPPLES = 110; // 距離抑制保持不變，這招很棒

    static class Blob {
        Rectangle rect;
        int pixelCount;
        public Blob(Rectangle rect, int pixelCount) {
            this.rect = rect;
            this.pixelCount = pixelCount;
        }
    }

    public static void main(String[] args) {
        String inputPath = "input.jpg"; 
        String outputPath = "debug_result.png";

        try {
            System.out.println("Loading image...");
            File inputFile = new File(inputPath);
            if (!inputFile.exists()) {
                System.out.println("Error: Cannot find image file: " + inputPath);
                return;
            }
            
            BufferedImage img = ImageIO.read(inputFile);
            int width = img.getWidth();
            int height = img.getHeight();
            boolean[][] visited = new boolean[width][height];
            List<Blob> rawDetections = new ArrayList<>();

            System.out.println("Image size: " + width + " x " + height);
            System.out.println("Scanning pixels and extracting features...");

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (!visited[x][y] && isPineappleColor(img.getRGB(x, y))) {
                        Blob blob = extractBlob(img, visited, x, y);
                        if (blob != null) {
                            rawDetections.add(blob);
                        }
                    }
                }
            }

            System.out.println("Raw detections before distance filter: " + rawDetections.size());

            rawDetections.sort((b1, b2) -> Integer.compare(b2.pixelCount, b1.pixelCount));
            List<Rectangle> finalDetections = filterByDistance(rawDetections);

            drawDebugOverlay(img, finalDetections);
            
            System.out.println("Scan complete! Final pineapples count: " + finalDetections.size());
            ImageIO.write(img, "png", new File(outputPath));
            System.out.println("Please check the debug output: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isPineappleColor(int rgb) {
        Color c = new Color(rgb);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        // 適度的色差過濾
        boolean isRedDominant = r > 45 && r < 145;
        boolean notGreenLeaf = g < (r - 18);
        boolean notBlueShadow = b < (r - 18); 
        
        return isRedDominant && notGreenLeaf && notBlueShadow;
    }

    private static Blob extractBlob(BufferedImage img, boolean[][] visited, int startX, int startY) {
        int width = img.getWidth();
        int height = img.getHeight();
        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        int count = 0; 

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startX, startY));
        visited[startX][startY] = true;

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            count++;
            
            if (p.x < minX) minX = p.x;
            if (p.x > maxX) maxX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.y > maxY) maxY = p.y;

            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i];
                int ny = p.y + dy[i];
                
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[nx][ny]) {
                    if (isPineappleColor(img.getRGB(nx, ny))) {
                        visited[nx][ny] = true;
                        queue.add(new Point(nx, ny));
                    }
                }
            }
        }

        int boxWidth = maxX - minX + 1;
        int boxHeight = maxY - minY + 1;
        if (boxWidth == 0 || boxHeight == 0) return null;

        // 長寬比維持合理的正方形到微長方形區間
        double aspectRatio = (double) boxWidth / boxHeight;
        boolean isSquareIsh = aspectRatio > 0.45 && aspectRatio < 2.2;

        // ★ 退回一點點的密度要求，允許綠葉遮擋造成的空洞
        int boxArea = boxWidth * boxHeight;
        double fillRatio = (double) count / boxArea;
        boolean isSolid = fillRatio > 0.36;

        if (count >= MIN_PIXEL_COUNT && count <= MAX_PIXEL_COUNT && isSquareIsh && isSolid) {
            return new Blob(new Rectangle(minX, minY, boxWidth, boxHeight), count);
        }
        return null; 
    }

    private static List<Rectangle> filterByDistance(List<Blob> sortedBlobs) {
        List<Rectangle> filtered = new ArrayList<>();
        
        for (Blob blob : sortedBlobs) {
            Rectangle rect = blob.rect;
            boolean isTooClose = false;
            int centerX1 = rect.x + rect.width / 2;
            int centerY1 = rect.y + rect.height / 2;

            for (Rectangle keptRect : filtered) {
                int centerX2 = keptRect.x + keptRect.width / 2;
                int centerY2 = keptRect.y + keptRect.height / 2;
                
                double distance = Math.sqrt(Math.pow(centerX1 - centerX2, 2) + Math.pow(centerY1 - centerY2, 2));
                
                if (distance < MIN_DISTANCE_BETWEEN_PINEAPPLES) {
                    isTooClose = true;
                    break;
                }
            }
            
            if (!isTooClose) {
                filtered.add(rect);
            }
        }
        return filtered;
    }

    private static void drawDebugOverlay(BufferedImage img, List<Rectangle> rects) {
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(4)); 

        for (int i = 0; i < rects.size(); i++) {
            Rectangle r = rects.get(i);
            g.drawRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.YELLOW);
            g.fillOval(r.x + r.width/2 - 4, r.y + r.height/2 - 4, 8, 8); 
            g.setColor(Color.RED); 
        }
        g.dispose();
    }
}