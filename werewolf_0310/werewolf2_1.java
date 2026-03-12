package werewolf_0312;
import java.util.Random;
import java.util.Scanner;

public class werewolf2_1 {

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

        public int getId() {
            return id;
        }

        public String getRole() {
            return role;
        }

        public boolean isAlive() {
            return alive;
        }

        public void kill() {
            alive = false;
        }

        public String getPublicInfo() {
            if (alive) {
                return "Player " + id + " [Alive]";
            } else {
                return "Player " + id + " [Dead]";
            }
        }
    }

    // ==========================================
    // 2. 主程式 (main)，遊戲真正執行的流程
    // ==========================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.println("wolfGame");
        System.out.println("Enter number of players(4~10)");
        
        int n = sc.nextInt();
        sc.nextLine(); // 吃掉換行符號

        // 檢查人數是否合法
        while (n < 4 || n > 10) {
            System.out.println("4~10");
            n = sc.nextInt();
            sc.nextLine();
        }

        // 建立陣列與隨機決定狼人
        Player[] players = new Player[n];
        int wolfIndex = rand.nextInt(n);

        for (int i = 0; i < n; i++) {
            if (i == wolfIndex) {
                players[i] = new Player(i + 1, "Werewolf");
            } else {
                players[i] = new Player(i + 1, "Villager");
            }
        }

        System.out.println();
        System.out.println("Role assignment start.");
        System.out.println("Each player take turn");

        // 輪流看身分
        for (int i = 0; i < n; i++) {
            System.out.println();
            System.out.println("Player " + (i + 1) + " Please Enter.");
            sc.nextLine();
            System.out.println("Your Role : " + players[i].getRole());
            System.out.println("Memorize your role , then turn.");
            sc.nextLine();
            
            // 印出30行空白洗畫面
            for (int line = 0; line < 30; line++) {
                System.out.println();
            }
        }

        boolean gameOver = false;
        int round = 1;

        // 遊戲主迴圈
        while (!gameOver) {
            System.out.println("Round " + round);
            System.out.println();

            // --- 黑夜階段 ---
            System.out.println("Night falls. Werewolf wakes up.");
            int aliveWolf = findAliveWolf(players);
            
            if (aliveWolf != -1) {
                System.out.println("Werewolf is your turn.");
                System.out.println("Alive players: ");
                printAlivePlayers(players);

                int targetId = -1;

                while (true) {
                    System.out.println("choose a player to kill");
                    if (sc.hasNextInt()) {
                        targetId = sc.nextInt();
                        System.out.println();
                        
                        // 檢查目標是否合法 (不能殺死人，狼人也不能殺自己)
                        if (isValidTarget(targetId, players, players[aliveWolf].getId())) {
                            break;
                        } else {
                            System.out.println("Invalid target. please again");
                            sc.nextLine();
                        }
                    } else {
                        sc.nextLine();
                    }
                }
                players[targetId - 1].kill();
                System.out.println("Night results: Player " + targetId + " has been killed.");
            } else {
                System.out.println("no werewolf alive. Skipping night phase");
            }

            // 檢查勝負
            if (checkVillagerWin(players)) {
                System.out.println("Villagers Win! Werewolf is dead.");
                gameOver = true;
                break;
            } else if (checkWerewolfWin(players)) {
                System.out.println();
                System.out.println("Werewolf wins! All villagers has been killed.");
                gameOver = true;
                break;
            }

            // --- 白天投票階段 ---
            System.out.println("\nDaytime begins. Discuss and vote for a player to execute.");
            printAlivePlayers(players);
            int voteId = -1;
            
            while (true) {
                System.out.println("choose a player to vote out");
                if (sc.hasNextInt()) {
                    voteId = sc.nextInt();
                    // 只要目標在範圍內且活著，就可以投票處決
                    if (voteId >= 1 && voteId <= players.length && players[voteId - 1].isAlive()) {
                        break;
                    } else {
                        System.out.println("Invalid target. please again");
                        sc.nextLine();
                    }
                } else {
                    sc.nextLine();
                }
            }
            players[voteId - 1].kill();
            System.out.println("Player " + voteId + " has been executed.");

            // 檢查勝負
            if (checkVillagerWin(players)) {
                System.out.println("Villagers Win! Werewolf is dead.");
                gameOver = true;
                break;
            } else if (checkWerewolfWin(players)) {
                System.out.println();
                System.out.println("Werewolf wins! All villagers has been killed.");
                gameOver = true;
                break;
            }

            round++;
        }
        sc.close();
    }

    // ==========================================
    // 3. 小幫手方法 (必須放在 main 方法的外面)
    // ==========================================

    public static int findAliveWolf(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive() && players[i].getRole().equals("Werewolf")) {
                return i;
            }
        }
        return -1;
    }

    public static void printAlivePlayers(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive()) {
                System.out.println(players[i].getPublicInfo());
            }
        }
    }

    public static boolean isValidTarget(int targetId, Player[] players, int wolfId) {
        if (targetId < 1 || targetId > players.length) return false;
        if (!players[targetId - 1].isAlive()) return false;
        if (targetId == wolfId) return false; // 狼人不能殺自己
        return true;
    }

    public static boolean checkVillagerWin(Player[] players) {
        return findAliveWolf(players) == -1; // 找不到活著的狼人，平民贏
    }

    public static boolean checkWerewolfWin(Player[] players) {
        int wolfCount = 0;
        int villagerCount = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive()) {
                if (players[i].getRole().equals("Werewolf")) {
                    wolfCount++;
                } else {
                    villagerCount++;
                }
            }
        }
        return wolfCount >= villagerCount; // 狼人數 >= 平民數，狼人贏
    }
}