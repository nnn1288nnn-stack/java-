    import java.util.Scanner;

// Requirement 3: At least one class
public class homework1_3 {

    // Requirement 2: At least one method
    // Requirement 1 & 6: Mathematical model (Nonlinear system) & Iterative computation
    public static double calculateSquareRoot(double n, int iterations) {
        double x = n; // 初始猜測值設為數字本身
        
        System.out.println("\n--- 開始迭代計算 ---");
        for (int i = 0; i < iterations; i++) {
            // 牛頓法非線性模型公式迭代
            x = 0.5 * (x + (n / x)); 
            
            // 顯示每一次迭代的逼近過程
            System.out.printf("第 %d 次迭代: 預估值 = %.6f\n", i + 1, x);
        }
        return x;
    }

    public static void main(String[] args) {
        // Requirement 4: Console input
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 牛頓法平方根計算器 ===");
        
        System.out.print("請輸入一個正數以計算其平方根: ");
        double number = scanner.nextDouble();

        System.out.print("請輸入想要迭代的次數 (例如 5 或 10): ");
        int iter = scanner.nextInt();

        if (number < 0 || iter <= 0) {
            System.out.println("請輸入大於 0 的有效數字！");
        } else {
            // 呼叫我們自定義的 Method
            double result = calculateSquareRoot(number, iter);
            
            // Requirement 5: Console output
            System.out.println("--------------------");
            System.out.printf("最終計算結果: %.2f 的平方根約為 %.6f\n", number, result);
            System.out.printf("系統內建函數 (Math.sqrt) 的結果作對比: %.6f\n", Math.sqrt(number));
        }

        // 關閉 Scanner 釋放資源
        scanner.close();
    }
}
    
