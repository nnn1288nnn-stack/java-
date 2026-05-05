import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MultiThresholdSegmentation {

    public static void main(String[] args) {
        try {
            // 1. 讀取原始影像 (請替換為您的圖片路徑)
            File inputFile = new File("input.jpg");
            BufferedImage image = ImageIO.read(inputFile);
            int width = image.getWidth();
            int height = image.getHeight();

            // 儲存灰階值的陣列與直方圖
            int[][] grayMatrix = new int[width][height];
            int[] histogram = new int[256];

            // 2. 轉灰階並統計直方圖
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    
                    // 簡單的灰階公式
                    int gray = (r + g + b) / 3; 
                    grayMatrix[x][y] = gray;
                    histogram[gray]++;
                }
            }

            // 3. 尋找最佳閾值 T_opt (此處需由您實作變異數計算邏輯)
            // 提示：根據簡報，您需要寫一個迴圈計算 "Within group variance" 來找出最佳 T 值
            int optimalThreshold = findOptimalThreshold(histogram, width * height);
            System.out.println("計算出的最佳閾值 (T_opt): " + optimalThreshold);

            // 4. 使用閾值進行影像分割 (前景與背景分離)
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (grayMatrix[x][y] >= optimalThreshold) {
                        // 前景 (例如設為白色)
                        outputImage.setRGB(x, y, 0xFFFFFF); 
                    } else {
                        // 背景 (例如設為黑色)
                        outputImage.setRGB(x, y, 0x000000); 
                    }
                }
            }

            // 5. 輸出結果影像
            File outputFile = new File("output_segmented.jpg");
            ImageIO.write(outputImage, "jpg", outputFile);
            System.out.println("影像分割完成！");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 您需要完成的核心演算法方法：計算組內變異數尋找閾值
    private static int findOptimalThreshold(int[] histogram, int totalPixels) {
        int bestThreshold = 128; // 預設值
        double minVariance = Double.MAX_VALUE;

        // TODO: 在此實作迴圈 (t = 0 to 255)
        // 1. 計算背景與前景的權重 (比例)
        // 2. 計算背景與前景的平均值
        // 3. 計算組內變異數 (Within group variance)
        // 4. 更新最小變異數與對應的 bestThreshold

        return bestThreshold; 
    }
}