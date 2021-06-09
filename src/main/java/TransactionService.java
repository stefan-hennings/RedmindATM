import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionService {
    private final Bank bank;
    
    public void attemptWithdrawal(int amountToWithdraw) {
        Withdrawal withdrawal = new Withdrawal(amountToWithdraw);
        if (isValidWithdrawalAmount(withdrawal)) {
            int amountRemaining = decideBillTypes(withdrawal.getAmount(), withdrawal);
    
            if (amountRemaining != 0) {
                withdrawal.decline("Not enough bills of the correct types to complete withdrawal. Try a different amount. ");
            } else {
                withdrawal.setApproved(true);
            }
        }
        
        processWithdrawal(withdrawal);
    }
    
    private boolean isValidWithdrawalAmount(Withdrawal withdrawal) {
        if (withdrawal.getAmount() <= 0 || withdrawal.getAmount() % 100 != 0) {
            withdrawal.decline("Invalid withdrawal amount. Must be a positive integer divisible by 100. ");
            return false;
        } else if (withdrawal.getAmount() > bank.getTotalValue()) {
            withdrawal.decline("Funds too low. Try a lower amount. ");
            return false;
        }
        return true;
    }
    
    private int decideBillTypes(int amountToWithdraw, Withdrawal withdrawal) {
        for (Integer denomination : bank.getSortedKeysDescending()) {
            int billsOfCurrentType = Integer.min(amountToWithdraw / denomination, bank.getBillQuantityOfDenomination(denomination));
            amountToWithdraw -= billsOfCurrentType * denomination;
            
            withdrawal.addBillType(denomination, billsOfCurrentType);
        }
        return amountToWithdraw;
    }
    
    public void processWithdrawal(Withdrawal withdrawal) {
        if (withdrawal.isApproved()) {
            bank.removeNotes(withdrawal);
        }
        System.out.println(withdrawal);
    }
}
