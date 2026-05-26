import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CartoonApp extends JFrame {
    
    private JLabel imageLabel;
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    
    // 控制參數
    private int currentBlurPasses = 3;
    private int currentEdgeThreshold = 85;
    private int currentColorLevels = 6;
    
    // 物件導向架構元件
    private final GrayConverter grayConverter;
    private final AdvancedEdgeDetector edgeDetector;
    private final DynamicColorQuantizer colorQuantizer;
    private final CartoonRenderer cartoonRenderer;

    public CartoonApp() {
        this.grayConverter = new GrayConverter();
        this.edgeDetector = new AdvancedEdgeDetector();
        this.colorQuantizer = new DynamicColorQuantizer();
        this.cartoonRenderer = new CartoonRenderer();

        setTitle("Interactive Cartoon Filter App (Traditional Image Processing)");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= 頂部面板：按鈕 =================
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 35, 25)); 
        JButton loadButton = new JButton("Load Image");
        JButton saveButton = new JButton("Save / Export");
        loadButton.setFocusable(false);
        saveButton.setFocusable(false);
        topPanel.add(loadButton);
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);

        // ================= 中央面板：影像顯示 =================
        imageLabel = new JLabel("Click 'Load Image' and use sliders to tune the effect!", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        imageLabel.setForeground(Color.GRAY);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        // ================= 底部面板：動態滑桿控制 =================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 1, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.setBackground(new Color(240, 240, 240));

        // 1. 平滑程度滑桿 (Blur Passes)
        JPanel blurPanel = createSliderPanel("1. Smoothness (Blur Passes)", 0, 10, currentBlurPasses, e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                currentBlurPasses = source.getValue();
                processCartoonEffect();
            }
        });
        
        // 2. 邊緣門檻滑桿 (Edge Threshold)
        JPanel edgePanel = createSliderPanel("2. Edge Sensitivity (Lower = More Lines)", 10, 200, currentEdgeThreshold, e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                currentEdgeThreshold = source.getValue();
                processCartoonEffect();
            }
        });
        
        // 3. 色彩階層滑桿 (Color Levels)
        JPanel colorPanel = createSliderPanel("3. Color Levels (Higher = Smoother Gradients)", 2, 16, currentColorLevels, e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                currentColorLevels = source.getValue();
                processCartoonEffect();
            }
        });

        bottomPanel.add(blurPanel);
        bottomPanel.add(edgePanel);
        bottomPanel.add(colorPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // ================= 按鈕事件 =================
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage rawImg = ImageIO.read(fileChooser.getSelectedFile());
                    if (rawImg != null) {
                        originalImage = resizeImageIfNeeded(rawImg, 800);
                        processCartoonEffect(); // 載入後自動渲染一次
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        saveButton.addActionListener(e -> {
            if (processedImage == null) return;
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    ImageIO.write(processedImage, "png", fileChooser.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Exported successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // 建立附帶標籤的滑桿面板工具
    private JPanel createSliderPanel(String title, int min, int max, int value, ChangeListener listener) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(listener);
        panel.add(label, BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    private BufferedImage resizeImageIfNeeded(BufferedImage src, int maxSide) {
        int w = src.getWidth(), h = src.getHeight();
        if (w <= maxSide && h <= maxSide) return src;
        int newW = w > h ? maxSide : (int) (((double) w / h) * maxSide);
        int newH = w > h ? (int) (((double) h / w) * maxSide) : maxSide;
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(src, 0, 0, newW, newH, null);
        g2d.dispose();
        return resized;
    }

    /**
     * 核心影像處理管線：現在會讀取最新的滑桿參數進行渲染
     */
    private void processCartoonEffect() {
        if (originalImage == null) return;

        // 將滑桿數值傳入各個演算法模組
        BufferedImage smoothImg = ImageSmoother.applyMultiSmooth(originalImage, currentBlurPasses);
        BufferedImage grayImg = grayConverter.toGray(smoothImg);
        BufferedImage edgeMask = edgeDetector.detectEdges(grayImg, currentEdgeThreshold);
        BufferedImage quantizedImg = colorQuantizer.quantizeWithContrast(smoothImg, currentColorLevels);
        processedImage = cartoonRenderer.render(quantizedImg, edgeMask);

        // GUI 畫面更新
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        int displayW = Math.min(w, 480);
        int displayH = (int) (((double) displayW / w) * h);

        BufferedImage combined = new BufferedImage(displayW * 2 + 20, displayH, BufferedImage.TYPE_INT_RGB);
        Graphics g = combined.getGraphics();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, combined.getWidth(), combined.getHeight());
        g.drawImage(originalImage, 0, 0, displayW, displayH, null);
        g.drawImage(processedImage, displayW + 20, 0, displayW, displayH, null);
        g.dispose();

        imageLabel.setIcon(new ImageIcon(combined));
        imageLabel.setText("");
    }

    // ==========================================
    // 演算法模組 (動態接收參數版)
    // ==========================================

    static class ImageSmoother {
        public static BufferedImage applyMultiSmooth(BufferedImage src, int passes) {
            BufferedImage current = src;
            for (int i = 0; i < passes; i++) {
                current = edgePreservingBlurPass(current);
            }
            return current;
        }

        private static BufferedImage edgePreservingBlurPass(BufferedImage src) {
            int w = src.getWidth(), h = src.getHeight();
            BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int colorTolerance = 60; 

            for (int y = 2; y < h - 2; y++) {
                for (int x = 2; x < w - 2; x++) {
                    Color center = new Color(src.getRGB(x, y));
                    int r = 0, g = 0, b = 0, count = 0;
                    for (int ky = -2; ky <= 2; ky++) {
                        for (int kx = -2; kx <= 2; kx++) {
                            Color neighbor = new Color(src.getRGB(x + kx, y + ky));
                            int dist = Math.abs(neighbor.getRed() - center.getRed()) +
                                       Math.abs(neighbor.getGreen() - center.getGreen()) +
                                       Math.abs(neighbor.getBlue() - center.getBlue());
                            if (dist < colorTolerance) {
                                r += neighbor.getRed(); g += neighbor.getGreen(); b += neighbor.getBlue();
                                count++;
                            }
                        }
                    }
                    dest.setRGB(x, y, new Color(r / count, g / count, b / count).getRGB());
                }
            }
            return dest;
        }
    }

    static class GrayConverter {
        public BufferedImage toGray(BufferedImage src) {
            int w = src.getWidth(), h = src.getHeight();
            BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = new Color(src.getRGB(x, y));
                    int gVal = (int) (0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue());
                    gray.setRGB(x, y, new Color(gVal, gVal, gVal).getRGB());
                }
            }
            return gray;
        }
    }

    static class AdvancedEdgeDetector {
        public BufferedImage detectEdges(BufferedImage gray, int threshold) {
            int w = gray.getWidth(), h = gray.getHeight();
            BufferedImage edges = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
            int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    int pX = 0, pY = 0;
                    for (int ky = -1; ky <= 1; ky++) {
                        for (int kx = -1; kx <= 1; kx++) {
                            int pixel = new Color(gray.getRGB(x + kx, y + ky)).getRed();
                            pX += pixel * sobelX[ky + 1][kx + 1];
                            pY += pixel * sobelY[ky + 1][kx + 1];
                        }
                    }
                    int magnitude = (int) Math.sqrt(pX * pX + pY * pY);
                    edges.setRGB(x, y, (magnitude > threshold) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            return edges;
        }
    }

    static class DynamicColorQuantizer {
        public BufferedImage quantizeWithContrast(BufferedImage src, int numLevels) {
            int w = src.getWidth(), h = src.getHeight();
            BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int step = 256 / Math.max(1, numLevels);

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = new Color(src.getRGB(x, y));
                    int r = (c.getRed() / step) * step + (step / 2);
                    int g = (c.getGreen() / step) * step + (step / 2);
                    int b = (c.getBlue() / step) * step + (step / 2);

                    r = Math.min(255, Math.max(0, r));
                    g = Math.min(255, Math.max(0, g));
                    b = Math.min(255, Math.max(0, b));
                    res.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
            return res;
        }
    }

    static class CartoonRenderer {
        public BufferedImage render(BufferedImage quantized, BufferedImage edgeMask) {
            int w = quantized.getWidth(), h = quantized.getHeight();
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (edgeMask.getRGB(x, y) == Color.BLACK.getRGB()) {
                        out.setRGB(x, y, Color.BLACK.getRGB());
                    } else {
                        out.setRGB(x, y, quantized.getRGB(x, y));
                    }
                }
            }
            return out;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CartoonApp().setVisible(true));
    }
}