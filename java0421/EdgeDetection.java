import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class EdgeDetection {
    public static void main(String[] args) {
        // 請確保您的專案目錄下有一張名為 "input.jpg" 的測試圖片
        String inputImagePath = "input.jpg";
        String outputImagePath = "output_edge.jpg";

        try {
            // 1. 讀取原始影像
            File inputFile = new File(inputImagePath);
            if (!inputFile.exists()) {
                System.out.println("找不到圖片檔案：" + inputImagePath);
                return;
            }
            BufferedImage originalImage = ImageIO.read(inputFile);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            
            // 建立用於存放邊緣檢測結果的影像
            BufferedImage edgeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // 2. 定義 Gradient operators (Sobel算子) [cite: 154, 159]
            // 水平方向的卷積核 (對應 X 的變化) [cite: 146, 157]
            int[][] sobelX = {
                { 1,  0, -1},
                { 2,  0, -2},
                { 1,  0, -1}
            };

            // 垂直方向的卷積核 (對應 Y 的變化) [cite: 158]
            int[][] sobelY = {
                { 1,  2,  1},
                { 0,  0,  0},
                {-1, -2, -1}
            };

            // 3. 進行卷積運算 (Convolution) 
            // 略過最外層的邊緣像素 1 pixel，避免陣列越界
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    int gradientX = 0;
                    int gradientY = 0;

                    // 套用 3x3 的遮罩計算
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            // 取得相鄰像素的 RGB 值並轉換為灰階
                            Color color = new Color(originalImage.getRGB(x + j, y + i));
                            // 常用的灰階轉換公式或是直接取平均
                            int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                            // 累加梯度值
                            // 注意：陣列索引需加上平移量 (+1) 以對應 0~2 的索引
                            gradientX += gray * sobelX[i + 1][j + 1];
                            gradientY += gray * sobelY[i + 1][j + 1];
                        }
                    }

                    // 4. 計算總梯度大小 (Magnitude)
                    // 簡報中包含 1/4 的權重 [cite: 157, 158]，但在計算整體強度變化時，通常會計算平方和的開根號再做正規化
                    int magnitude = (int) Math.sqrt((gradientX * gradientX) + (gradientY * gradientY));
                    
                    // 將結果限制在 0~255 的有效色彩範圍內
                    magnitude = Math.min(255, Math.max(0, magnitude));

                    // 5. 寫入新影像
                    Color edgeColor = new Color(magnitude, magnitude, magnitude);
                    edgeImage.setRGB(x, y, edgeColor.getRGB());
                }
            }

            // 6. 輸出處理後的影像
            File outputFile = new File(outputImagePath);
            ImageIO.write(edgeImage, "jpg", outputFile);
            System.out.println("邊緣檢測處理完成！結果已儲存為: " + outputImagePath);

        } catch (Exception e) {
            System.err.println("處理圖片時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
}