package werewolf_0312;
import java.util.Random;
import java.util.Scanner;

public class werewolf2_2 {

    // ==========================================
    // 1. 玩家藍圖 (Player Class) - 包含封裝與陣營
    // ==========================================
    static class Player {
        private int id;
        private String role;
        private String camp; // 陣營: "Good" 或 "Bad"
        private boolean alive;

        public Player(int id, String role, String camp) {
            this.id = id;
            this.role = role;
            this.camp = camp;
            this.alive = true;
        }

        public int getId() { return id; }
        public String getRole() { return role; }
        public String getCamp() { return camp; }
        public boolean isAlive() { return alive; }
        public void kill() { alive = false; }

        public String getPublicInfo() {
            if (alive) {
                return "Player " + id + " [Alive]";
            } else {
                return "Player " + id + " [Dead]";
            }
        }
    }

    // ==========================================
    // 2. 主程式 (main) - 遊戲流程控制
    // ==========================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.println("=== 歡迎來到終極狼人殺 (Assignment 2) ===");
        
        // --- 設定角色數量 (Configurable role counts) ---
        System.out.print("請輸入【狼人 (Werewolf - Bad)】的數量: ");
        int numWolves = sc.nextInt();
        System.out.print("請輸入【預言家 (Seer - Good)】的數量: ");
        int numSeers = sc.nextInt();
        System.out.print("請輸入【平民 (Villager - Good)】的數量: ");
        int numVillagers = sc.nextInt();
        sc.nextLine(); // 吃掉換行符號

        int totalPlayers = numWolves + numSeers + numVillagers;
        if (totalPlayers < 4) {
            System.out.println("警告：總人數太少，遊戲可能很快就結束了！");
        }

        // --- 準備身分牌庫 ---
        String[] roles = new String[totalPlayers];
        String[] camps = new String[totalPlayers];
        int index = 0;
        
        // 放入狼人
        for (int i = 0; i < numWolves; i++) {
            roles[index] = "Werewolf";
            camps[index] = "Bad";
            index++;
        }
        // 放入預言家
        for (int i = 0; i < numSeers; i++) {
            roles[index] = "Seer";
            camps[index] = "Good";
            index++;
        }
        // 放入平民
        for (int i = 0; i < numVillagers; i++) {
            roles[index] = "Villager";
            camps[index] = "Good";
            index++;
        }

        // --- 洗牌 (打亂身分) ---
        for (int i = 0; i < totalPlayers; i++) {
            int r = rand.nextInt(totalPlayers);
            // 交換角色
            String tempRole = roles[i];
            roles[i] = roles[r];
            roles[r] = tempRole;
            // 交換陣營
            String tempCamp = camps[i];
            camps[i] = camps[r];
            camps[r] = tempCamp;
        }

        // --- 建立玩家陣列 (Java Arrays & Objects) ---
        Player[] players = new Player[totalPlayers];
        for (int i = 0; i < totalPlayers; i++) {
            players[i] = new Player(i + 1, roles[i], camps[i]);
        }

        // --- 秘密看牌階段 ---
        System.out.println("\n--- 角色分配開始 ---");
        for (int i = 0; i < totalPlayers; i++) {
            System.out.println("Player " + (i + 1) + " 請按 Enter 鍵查看身分...");
            sc.nextLine();
            System.out.println("你的身分是 : [" + players[i].getRole() + "] (陣營: " + players[i].getCamp() + ")");
            System.out.println("記住你的身分後，請按 Enter 換下一位...");
            sc.nextLine();
            
            // 印出30行空白洗掉畫面
            for (int line = 0; line < 30; line++) {
                System.out.println();
            }
        }

        boolean gameOver = false;
        int round = 1;

        // --- 遊戲主迴圈 (Game Loop) ---
        while (!gameOver) {
            System.out.println("====== 第 " + round + " 回合 ======");
            
            // 變數記錄晚上誰被殺了
            int killedTonight = -1;

            // --- 黑夜階段：預言家 (Night abilities) ---
            System.out.println("\n天黑請閉眼...");
            System.out.println("【預言家】請睜眼。");
            int aliveSeerIndex = findAliveRole(players, "Seer");
            
            if (aliveSeerIndex != -1) {
                System.out.println("預言家 (Player " + players[aliveSeerIndex].getId() + ")，請選擇你要查驗的玩家 ID (輸入 0 放棄): ");
                printAlivePlayers(players);
                int checkId = sc.nextInt();
                sc.nextLine();
                
                if (checkId > 0 && isValidTarget(checkId, players, -1)) {
                    System.out.println("-> Player " + checkId + " 的真實身分是: " + players[checkId - 1].getRole());
                } else {
                    System.out.println("-> 無效的目標或放棄查驗。");
                }
            } else {
                System.out.println("(預言家已死亡或不存在，等待 3 秒...)");
                // 模擬等待，不讓大家發現預言家死了
                try { Thread.sleep(3000); } catch (Exception e) {} 
            }
            System.out.println("【預言家】請閉眼。\n");

            // --- 黑夜階段：狼人 (Night abilities) ---
            System.out.println("【狼人】請睜眼。");
            int aliveWolfIndex = findAliveRole(players, "Werewolf");
            
            if (aliveWolfIndex != -1) {
                System.out.println("狼人，請選擇你要殺害的玩家 ID: ");
                printAlivePlayers(players);
                
                while (true) {
                    if (sc.hasNextInt()) {
                        killedTonight = sc.nextInt();
                        sc.nextLine();
                        if (isValidTarget(killedTonight, players, players[aliveWolfIndex].getId())) {
                            break;
                        } else {
                            System.out.println("無效的目標 (不能殺死人，狼人不能殺自己)，請重新輸入！");
                        }
                    } else {
                        sc.nextLine();
                    }
                }
                players[killedTonight - 1].kill(); // 執行殺害
            } else {
                System.out.println("(沒有存活的狼人...)");
            }
            System.out.println("【狼人】請閉眼。\n");

            // --- 檢查勝負 ---
            if (checkWinCondition(players)) break;

            // --- 白天階段 (Day voting system) ---
            System.out.println("天亮了！");
            if (killedTonight != -1) {
                System.out.println("昨晚，Player " + killedTonight + " 慘遭殺害！");
            } else {
                System.out.println("昨晚是個平安夜，沒有人死亡。");
            }

            // 再次檢查勝負 (因為晚上有人死了)
            if (checkWinCondition(players)) break;

            System.out.println("\n請倖存者開始討論，並投票處決一名嫌疑犯！");
            printAlivePlayers(players);
            int voteId = -1;
            
            while (true) {
                System.out.print("請輸入大家決定要處決的玩家 ID (輸入 0 棄票): ");
                if (sc.hasNextInt()) {
                    voteId = sc.nextInt();
                    sc.nextLine();
                    if (voteId == 0) {
                        System.out.println("大家決定棄票，本回合無人被處決。");
                        break;
                    } else if (isValidTarget(voteId, players, -1)) {
                        players[voteId - 1].kill();
                        System.out.println("投票結果出爐：Player " + voteId + " 被送上了斷頭台。");
                        break;
                    } else {
                        System.out.println("無效的目標，請重新輸入！");
                    }
                } else {
                    sc.nextLine();
                }
            }

            // --- 檢查勝負 ---
            if (checkWinCondition(players)) break;

            round++;
        }
        
        System.out.println("=== 遊戲結束 ===");
        sc.close();
    }

    // ==========================================
    // 3. 小幫手方法 (Methods)
    // ==========================================

    // 尋找某個特定角色是否還有活著的人 (回傳第一個找到的索引)
    public static int findAliveRole(Player[] players, String targetRole) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive() && players[i].getRole().equals(targetRole)) {
                return i;
            }
        }
        return -1;
    }

    // 印出所有活著的玩家
    public static void printAlivePlayers(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive()) {
                System.out.println(players[i].getPublicInfo());
            }
        }
    }

    // 檢查目標是否合法
    public static boolean isValidTarget(int targetId, Player[] players, int selfId) {
        if (targetId < 1 || targetId > players.length) return false;
        if (!players[targetId - 1].isAlive()) return false;
        if (targetId == selfId) return false; // 不能選自己
        return true;
    }

    // 檢查勝負條件 (Win-condition detection)
    public static boolean checkWinCondition(Player[] players) {
        int goodCount = 0;
        int badCount = 0;

        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive()) {
                if (players[i].getCamp().equals("Bad")) {
                    badCount++;
                } else {
                    goodCount++;
                }
            }
        }

        if (badCount == 0) {
            System.out.println("\n🎉 【好人陣營 (Good)】獲勝！所有的狼人都被消滅了！");
            return true;
        } else if (badCount >= goodCount) {
            System.out.println("\n🐺 【壞人陣營 (Bad)】獲勝！狼人數已經壓制好人，屠村成功！");
            return true;
        }
        
        return false; // 遊戲繼續
    }
}