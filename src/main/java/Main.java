import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Integer> defaultBills = new HashMap<>();
        defaultBills.put(1000, 2);
        defaultBills.put(500, 3);
        defaultBills.put(100, 5);
        
        TransactionService transactionService = new TransactionService(new Bank(defaultBills));
        
        transactionService.attemptWithdrawal(1500);
        transactionService.attemptWithdrawal(700);
        transactionService.attemptWithdrawal(400);
        transactionService.attemptWithdrawal(1100);
        transactionService.attemptWithdrawal(1000);
        transactionService.attemptWithdrawal(700);
        transactionService.attemptWithdrawal(300);
        System.out.println(transactionService.getBank().getTotalValue());
    }
}
