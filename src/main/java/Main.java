import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Integer> defaultBills = new HashMap<>();
        defaultBills.put(1000, 2);
        defaultBills.put(500, 3);
        defaultBills.put(100, 5);
        
        TransactionController transactionController = new TransactionController(new Bank(defaultBills));
        
        transactionController.attemptWithdrawal(1500);
        transactionController.attemptWithdrawal(700);
        transactionController.attemptWithdrawal(400);
        transactionController.attemptWithdrawal(1100);
        transactionController.attemptWithdrawal(1000);
        transactionController.attemptWithdrawal(700);
        transactionController.attemptWithdrawal(300);
        System.out.println(transactionController.getBank().getTotalValue());
    }
}
