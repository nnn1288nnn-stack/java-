import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// ==========================================
// 1. 基礎類別：示範「封裝 (Encapsulation)」
// ==========================================
class User {
    // protected：允許子類別(Admin)存取
    protected String username;
    
    // private：最高層級保護，外部無法直接讀取或修改密碼
    private String password; 

    // 建構子
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 方法
    public String getUsername() {
        return username;
    }

    // 驗證密碼的方法，而不是直接把密碼 return 出去
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // 準備給多型 (Polymorphism) 使用的方法
    public void displayRole() {
        System.out.println("Current Role: Regular User");
    }
}

// ==========================================
// 2. 子類別：示範「繼承 (Inheritance)」
// ==========================================
class Admin extends User {
    
    public Admin(String username, String password) {
        super(username, password); // 呼叫父類別的建構子
    }

    // 覆寫 (Override) 父類別的方法：示範「多型 (Polymorphism)」
    @Override
    public void displayRole() {
        System.out.println("Current Role: Administrator - Full privileges granted!");
    }

    // 管理員專屬的特殊功能
    public void showSystemStatus() {
        System.out.println("[System Notification] System is running normally. No abnormal logins detected.");
    }
}

// ==========================================
// 3. 主程式：示範「集合 (ArrayList)」與「例外處理 (Exception Handling)」
// ==========================================
public class Main {
    public static void main(String[] args) {
        // 使用 ArrayList 來動態儲存不同種類的使用者 (包含 User 與 Admin)
        ArrayList<User> userList = new ArrayList<>();
        
        // 預先建立兩個帳號作為測試
        userList.add(new User("student", "1234"));
        userList.add(new Admin("admin", "root"));

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("=== Welcome to the Java Login System ===");

        // try-catch-finally 結構
        try {
            while (isRunning) {
                System.out.println("\n1. Login");
                System.out.println("2. Exit");
                System.out.print("Please select an option (1-2): ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // 清除換行字元

                if (choice == 1) {
                    System.out.print("Enter username: ");
                    String inputUser = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String inputPass = scanner.nextLine();

                    boolean loginSuccess = false;

                    // 走訪 ArrayList 檢查帳號密碼
                    for (User user : userList) {
                        if (user.getUsername().equals(inputUser) && user.checkPassword(inputPass)) {
                            System.out.println("\nLogin successful! Welcome, " + user.getUsername() + ".");
                            
                            // 示範多型：雖然變數型態都是 User，但如果是 Admin 登入，會印出管理員的版本
                            user.displayRole(); 

                            // 如果是管理員，執行專屬功能 (需要轉型 Casting)
                            if (user instanceof Admin) {
                                ((Admin) user).showSystemStatus();
                            }
                            
                            loginSuccess = true;
                            break;
                        }
                    }

                    if (!loginSuccess) {
                        System.out.println("Login failed: Incorrect username or password!");
                    }

                } else if (choice == 2) {
                    System.out.println("Shutting down the system. Goodbye!");
                    isRunning = false;
                } else {
                    System.out.println("Invalid option, please try again.");
                }
            }
        } catch (InputMismatchException e) {
            // 例外處理：捕捉使用者亂輸入文字（非數字）導致的崩潰
            System.out.println("\n[Error] Please enter a valid number! System forcefully terminated.");
        } finally {
            // Finally：無論程式是正常結束還是因為錯誤崩潰，都會執行這裡來關閉資源
            System.out.println(">> Closing Scanner resource...");
            scanner.close();
        }
    }
}