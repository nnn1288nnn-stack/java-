import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class PineappleCounter {

    // 面積限制 (保持放寬狀態，確保不漏抓)
    private static final int MIN_PIXEL_COUNT = 20;   
    private static final int MAX_PIXEL_COUNT = 600;  

    // ★ 關鍵修改：針對 4K 圖片，將最小距離大幅拉高到 110 像素
    private static final int MIN_DISTANCE_BETWEEN_PINEAPPLES = 110;

    // 新增一個內部類別，用來同時儲存「框框」和「紫紅像素數量(信心度)」
    static class Blob {
        Rectangle rect;
        int pixelCount; // 像素越多，代表越有可能是真正的鳳梨中心
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

            // ★ 關鍵修改：在過濾距離前，先按照像素數量(信心度)從大到小排序！
            // 這樣可以確保在清除重疊雜訊時，留下來的都是最紮實的鳳梨。
            rawDetections.sort((b1, b2) -> Integer.compare(b2.pixelCount, b1.pixelCount));

            // 進行距離過濾
            List<Rectangle> finalDetections = filterByDistance(rawDetections);

            drawDebugOverlay(img, finalDetections);
            
            System.out.println("Scan complete! Final pineapples count: " + finalDetections.size());
            ImageIO.write(img, "png", new File(outputPath));
            System.out.println("Please check the debug output: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 顏色判斷 (維持前一版的放寬狀態)
    private static boolean isPineappleColor(int rgb) {
        Color c = new Color(rgb);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        boolean isRedDominant = r > 40 && r < 145;
        boolean notGreenLeaf = g < (r - 15);
        boolean notBlueShadow = b < (r - 15); 
        
        return isRedDominant && notGreenLeaf && notBlueShadow;
    }

    // 區域生長演算法 (回傳 Blob 而非 Rectangle)
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

        double aspectRatio = (double) boxWidth / boxHeight;
        boolean isSquareIsh = aspectRatio > 0.35 && aspectRatio < 2.8;

        int boxArea = boxWidth * boxHeight;
        double fillRatio = (double) count / boxArea;
        boolean isSolid = fillRatio > 0.30;

        if (count >= MIN_PIXEL_COUNT && count <= MAX_PIXEL_COUNT && isSquareIsh && isSolid) {
            // ★ 修改：回傳包含像素數量的 Blob 物件
            return new Blob(new Rectangle(minX, minY, boxWidth, boxHeight), count);
        }
        return null; 
    }

    // 距離過濾邏輯
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
                
                // ★ 距離小於 110 像素的，一律視為同一個物件的雜訊並丟棄
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
        g.setStroke(new BasicStroke(4)); // 為了 4K 畫質，稍微加粗框線

        for (int i = 0; i < rects.size(); i++) {
            Rectangle r = rects.get(i);
            g.drawRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.YELLOW);
            g.fillOval(r.x + r.width/2 - 4, r.y + r.height/2 - 4, 8, 8); // 加大中心黃點
            g.setColor(Color.RED); 
        }
        g.dispose();
    }
}