public class KisaragiStationModel {

    // 定義三維真實世界的座標 (例如蓮實在現實中的位置)
    static class RealWorldPoint {
        double x, y, z;
        public RealWorldPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z; // Z 代表回到現實的深度(距離)
        }
        @Override
        public String toString() {
            return String.format("真實世界座標(X:%.1f, Y:%.1f, Z:%.1f)", x, y, z);
        }
    }

    // 定義如月車站 (投影面) 的二維座標
    static class KisaragiPlanePoint {
        double x, y;
        public KisaragiPlanePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return String.format("如月車站座標(x:%.1f, y:%.1f) -> 深度資訊(Z)已永遠遺失", x, y);
        }
    }

    // 針孔相機模型：代表那條連接兩界的長隧道
    static class TunnelPinholeCamera {
        private final double focalLength; // 隧道的焦距(空間扭曲係數)

        public TunnelPinholeCamera(double focalLength) {
            this.focalLength = focalLength;
        }

        // 投影方法：將真實世界的 3D 點投影到如月車站的 2D 面
        public KisaragiPlanePoint enterTheTunnel(RealWorldPoint realPos) {
            if (realPos.z == 0) {
                throw new RuntimeException("奇點崩潰：無法停留在針孔(隧道)正中央！");
            }
            
            // 針孔投影公式： x' = f * (X / Z), y' = f * (Y / Z)
            // 注意：因為穿過針孔，影像通常是倒立反轉的，這也符合異世界的詭異感
            double projectedX = -focalLength * (realPos.x / realPos.z);
            double projectedY = -focalLength * (realPos.y / realPos.z);
            
            return new KisaragiPlanePoint(projectedX, projectedY);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 如月車站：Pinhole Camera Model 降維模擬 ===");

        // 1. 設定模型環境
        double tunnelFocalLength = 50.0; // 隧道的投影焦距
        TunnelPinholeCamera mysteriousTunnel = new TunnelPinholeCamera(tunnelFocalLength);

        // 2. 蓮實在現實世界搭上電車時的位置 (X, Y, 距離家鄉的深度Z)
        RealWorldPoint hasumiRealPos = new RealWorldPoint(120.0, 50.0, 200.0);
        System.out.println("2004年1月8日 深夜");
        System.out.println("蓮實的初始位置： " + hasumiRealPos);
        System.out.println("--------------------------------------------------");

        // 3. 經過異常的長隧道 (進入針孔)
        System.out.println("「平時五分鐘就到的站，今天卻開了很久，經過了一條沒見過的隧道...」");
        KisaragiPlanePoint hasumiKisaragiPos = mysteriousTunnel.enterTheTunnel(hasumiRealPos);

        // 4. 抵達如月車站
        System.out.println("--------------------------------------------------");
        System.out.println("蓮實已抵達異世界投影面： " + hasumiKisaragiPos);
        System.out.println("座標已被倒轉 (負號)，世界看起來一樣卻又有些不對勁。");

        // 5. 證明她無法回頭 (數學上的不可逆性)
        System.out.println("--------------------------------------------------");
        System.out.println("【系統嘗試將如月車站座標逆轉換回真實世界】");
        attemptToReturnHome(hasumiKisaragiPos, tunnelFocalLength);
    }

    // 嘗試逆投影 (這注定會失敗，因為缺乏 Z)
    public static void attemptToReturnHome(KisaragiPlanePoint pos, double f) {
        System.out.println("逆推算式： X = (x * Z) / f,  Y = (y * Z) / f");
        System.out.println("嘗試解開聯立方程式...");
        System.out.println("結果：無法計算！因為 Z (深度) 變數未知。");
        System.out.println("結論：這是一條射線上的無限多種可能，她被困在了二維的影像平面上，這就是如月車站沒有出口的原因。");
    }
}