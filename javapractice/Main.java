import java.util.Scanner;
import java.util.Random; // 記得匯入 Random

public class Main {

    // ==========================================
    // 1. 玩家藍圖 (Player Class)
    // ==========================================
    static class Player {
        private int id;
        private String role;
        private boolean alive;

        public Player(int id, String role) {
            this.id = id;
            this.role = role;
            this.alive = true;
        }

        public int getId() { return id; }
        public String getRole() { return role; }
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
    // 2. 主程式 (遊戲流程)
    // ==========================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.println("=== 歡迎來到終極狼人殺 ===");
        System.out.print("請輸入玩家總人數 (4~10): ");
        int n = sc.nextInt();
        
        while (n < 4 || n > 10) {
            System.out.print("人數錯誤，請重新輸入 (4~10): ");
            n = sc.nextInt();
        }

        // 建立玩家陣列
        Player[] players = new Player[n];
        
        // 隨機抽籤決定誰是狼人 (0 到 n-1 之間抽一個數字)
        int wolfIndex = rand.nextInt(n); 

        for (int i = 0; i < n; i++) {
            if (i == wolfIndex) {
                players[i] = new Player(i + 1, "Werewolf"); // 狼人
            } else {
                players[i] = new Player(i + 1, "Villager"); // 村民
            }
        }

        // 秘密發牌階段 (暫時註解掉清空畫面的功能，方便測試)
        System.out.println("\n--- 角色分配完畢 ---");
        for (int i = 0; i < n; i++) {
            System.out.println("Player " + (i + 1) + " 的身分是: " + players[i].getRole());
        }
        System.out.println("(正式遊戲時，這裡應該讓每個人輪流看身分，然後清空螢幕)");
        System.out.println("--------------------\n");

        boolean gameOver = false;
        int round = 1;

        // 遊戲迴圈開始
        while (!gameOver) {
            System.out.println("====== 第 " + round + " 回合 ======");
            
            // 【黑夜階段】
            System.out.println("天黑請閉眼。狼人現身...");
            int aliveWerewolfIndex = findAliveWolfIndex(players);

            if (aliveWerewolfIndex != -1) {
                System.out.println("狼人，輪到你了。目前存活的玩家有：");
                printAlivePlayers(players);

                int targetId = -1;
                while (true) {
                    System.out.print("請輸入你想殺害的玩家 ID: ");
                    targetId = sc.nextInt();
                    
                    if (isValidTarget(targetId, players)) {
                        break; // 目標正確，跳出輸入迴圈
                    } else {
                        System.out.println("無效的目標 (可能不存在或已死亡)，請重新輸入！");
                    }
                }
                players[targetId - 1].kill(); // 殺死目標
                System.out.println("黑夜結束。Player " + targetId + " 慘遭殺害。");
            } else {
                System.out.println("狼人已全數陣亡，跳過黑夜。");
            }

            // 檢查勝負
            if (checkVillagerWin(players)) {
                System.out.println("\n🎉 村民獲勝！所有的狼人都被消滅了！");
                break;
            } else if (checkWerewolfWin(players)) {
                System.out.println("\n🐺 狼人獲勝！狼人數大於等於村民數，屠村成功！");
                break;
            }

            // 【白天投票階段】(原本程式碼漏掉的關鍵)
            System.out.println("\n天亮了。所有倖存者請開始討論並投票找出狼人！");
            printAlivePlayers(players);
            int voteId = -1;
            while (true) {
                System.out.print("請輸入大家決定要處決的玩家 ID: ");
                voteId = sc.nextInt();
                if (isValidTarget(voteId, players)) {
                    break;
                } else {
                    System.out.println("無效的目標，請重新投票！");
                }
            }
            players[voteId - 1].kill(); // 處決目標
            System.out.println("投票結果出爐：Player " + voteId + " 被送上了斷頭台。");

            // 再次檢查勝負
            if (checkVillagerWin(players)) {
                System.out.println("\n🎉 村民獲勝！所有的狼人都被消滅了！");
                break;
            } else if (checkWerewolfWin(players)) {
                System.out.println("\n🐺 狼人獲勝！狼人數大於等於村民數，屠村成功！");
                break;
            }

            round++;
        }
        
        sc.close();
    }

    // ==========================================
    // 3. 遊戲小幫手方法 (必須放在 main 的外面)
    // ==========================================

    // 尋找還活著的狼人，回傳他的索引值(0~n-1)。找不到就回傳 -1
    public static int findAliveWolfIndex(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive() && players[i].getRole().equals("Werewolf")) {
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