import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class VisualHeightMeasure extends JPanel {

    private BufferedImage image;
    private ArrayList<Point> points = new ArrayList<>();
    private String message = "第1步：請點擊地板上某條線的【起點】(例如地磚邊緣)";
    private double calculatedHeight = 0;

    // 定義點與線的數學模型
    static class Point2D {
        double x, y;
        public Point2D(double x, double y) { this.x = x; this.y = y; }
    }

    static class Line {
        double a, b, c;
        public Line(double a, double b, double c) { this.a = a; this.b = b; this.c = c; }
        public static Line fromPoints(Point2D p1, Point2D p2) {
            return new Line(p1.y - p2.y, p2.x - p1.x, p1.x * p2.y - p2.x * p1.y);
        }
    }

    public static Point2D intersection(Line l1, Line l2) {
        double det = l1.a * l2.b - l2.a * l1.b;
        if (Math.abs(det) < 1e-9) throw new RuntimeException("線條平行，無法產生交點");
        return new Point2D((l1.b * l2.c - l2.b * l1.c) / det, (l2.a * l1.c - l1.a * l2.c) / det);
    }

    public static double distance(Point2D p1, Point2D p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public VisualHeightMeasure(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            System.out.println("載入圖片失敗，請確認檔名: " + imagePath);
            System.exit(1);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (points.size() < 8) {
                    points.add(e.getPoint());
                    updateMessage();
                    repaint();
                    
                    if (points.size() == 8) {
                        calculateFinalHeight();
                    }
                }
            }
        });
    }

    private void updateMessage() {
        int step = points.size();
        switch (step) {
            case 1: message = "第2步：請點擊同一條線往遠處延伸的【終點】"; break;
            case 2: message = "第3步：請點擊地板上另一條平行線的【起點】(用來找消失點)"; break;
            case 3: message = "第4步：請點擊另一條平行線往遠處延伸的【終點】"; break;
            case 4: message = "第5步：請點擊【180cm參考同學】的 頭頂"; break;
            case 5: message = "第6步：請點擊【180cm參考同學】的 腳底"; break;
            case 6: message = "第7步：請點擊【目標同學】的 頭頂"; break;
            case 7: message = "第8步：請點擊【目標同學】的 腳底"; break;
            case 8: message = "計算完成！"; break;
        }
    }

    private void calculateFinalHeight() {
        try {
            // 1. 利用地板的兩條平行線找出消失點 (Vanishing Point)，其 Y 座標即為地平線
            Line groundLine1 = Line.fromPoints(new Point2D(points.get(0).x, points.get(0).y), new Point2D(points.get(1).x, points.get(1).y));
            Line groundLine2 = Line.fromPoints(new Point2D(points.get(2).x, points.get(2).y), new Point2D(points.get(3).x, points.get(3).y));
            Point2D vanishingPoint = intersection(groundLine1, groundLine2);
            Line horizonLine = new Line(0, 1, -vanishingPoint.y); // y = vanishingPoint.y

            // 2. 獲取人物座標
            Point2D refTop = new Point2D(points.get(4).x, points.get(4).y);
            Point2D refBottom = new Point2D(points.get(5).x, points.get(5).y);
            Point2D targetTop = new Point2D(points.get(6).x, points.get(6).y);
            Point2D targetBottom = new Point2D(points.get(7).x, points.get(7).y);

            // 3. 套用公式計算
            Line lFeet = Line.fromPoints(refBottom, targetBottom);
            Point2D vPointFeet = intersection(lFeet, horizonLine);
            Line lTop = Line.fromPoints(vPointFeet, refTop);
            Line lVert = Line.fromPoints(targetBottom, targetTop);
            Point2D virtualRefTop = intersection(lTop, lVert);
            
            double targetPixelHeight = distance(targetTop, targetBottom);
            double virtualRefPixelHeight = distance(virtualRefTop, targetBottom);
            
            calculatedHeight = 180.0 * (targetPixelHeight / virtualRefPixelHeight);
            repaint();

        } catch (Exception ex) {
            message = "計算錯誤: 點擊的地板線可能互相平行，請重新執行程式。";
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 畫出圖片
        if (image != null) {
            g2d.drawImage(image, 0, 0, this);
        }

        // 畫出提示文字背景與文字
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, 10, 550, 40);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2d.drawString(message, 20, 38);

        // 畫出點擊的標記點
        g2d.setColor(Color.RED);
        for (Point p : points) {
            g2d.fillOval(p.x - 4, p.y - 4, 8, 8);
        }

        // 畫出地板輔助線 (尋找地平線用)
        g2d.setStroke(new BasicStroke(2));
        if (points.size() >= 2) g2d.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
        if (points.size() >= 4) g2d.drawLine(points.get(2).x, points.get(2).y, points.get(3).x, points.get(3).y);

        // 如果計算完成，顯示身高結果
        if (points.size() == 8 && calculatedHeight > 0) {
            g2d.setColor(new Color(0, 150, 0, 200));
            g2d.fillRect(points.get(6).x, points.get(6).y - 60, 250, 50);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
            g2d.drawString(String.format("預估身高: %.1f cm", calculatedHeight), points.get(6).x + 10, points.get(6).y - 25);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return image == null ? new Dimension(800, 600) : new Dimension(image.getWidth(), image.getHeight());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("單相機身高測量系統");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // 這裡替換成你要測量的圖片檔名
            VisualHeightMeasure panel = new VisualHeightMeasure("pic1 (1).jpg"); 
            
            JScrollPane scrollPane = new JScrollPane(panel);
            frame.add(scrollPane);
            frame.pack();
            // 限制視窗最大尺寸避免圖片超過螢幕
            frame.setSize(Math.min(panel.getPreferredSize().width + 20, 1200), 
                          Math.min(panel.getPreferredSize().height + 40, 800));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}