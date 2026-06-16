package farmgame;

/**
 * 地圖網格單元，包含地形種類與農場狀態
 */
public class Tile {

    // === 地形列舉 ===
    public enum TileType {
        GRASS,  // 草地（可通行、可耕作）
        WALL    // 障礙物（不可通行）
    }

    // === 農場狀態機 ===
    // 一般草地 → 翻土 → 播種 → 澆水 → 成長中 → 成熟可收成
    public enum FarmState {
        NONE,    // 一般草地（尚未耕作）
        TILLED,  // 已翻土
        PLANTED, // 已播種
        WATERED, // 已澆水
        GROWING, // 成長中
        MATURE   // 成熟可收成
    }

    // ---- 執行個體欄位 ----
    public TileType type;
    public FarmState farmState = FarmState.NONE;
    public String cropType = "";   // 作物名稱（例如「防風草」）
    public int growthStage = 0;    // 當前成長階段 (1 ~ TOTAL_GROWTH_STAGES)
    public int growthTimerMs = 0;  // 成長計時累積 (毫秒)

    // ---- 成長時間常數 ----
    public static final int TIME_WATERED_TO_GROWING = 10_000; // 澆水 → 開始成長 需 10 秒
    public static final int TIME_PER_GROWTH_STAGE    =  2_500; // 每個成長階段 需 2.5 秒
    public static final int TOTAL_GROWTH_STAGES      =      4; // 一共 4 個成長階段

    public Tile(TileType type) {
        this.type = type;
    }
}
