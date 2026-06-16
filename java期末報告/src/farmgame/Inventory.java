package farmgame;

/**
 * 背包系統 — 儲存金幣與收成的農作物數量
 */
public class Inventory {

    public int gold      = 0;   // 金幣
    public int cropCount = 0;   // 背包中的作物數量

    /** 收成一個作物 */
    public void harvestCrop() {
        cropCount++;
    }

    /**
     * 將背包中所有作物賣出
     * @return 賣出的數量（傳回值供訊息提示用）
     */
    public int sellAll() {
        int sold = cropCount;
        if (sold > 0) {
            gold += sold * 10;   // 每個作物 10 金幣
            cropCount = 0;
        }
        return sold;
    }
}
