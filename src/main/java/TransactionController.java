import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionController {
    private final Bank bank;
    
    public void attemptWithdrawal(int amountToWithdraw) {
        Withdrawal withdrawal = validateWithdrawal(amountToWithdraw);
        processWithdrawal(withdrawal);
    }
    
    public Withdrawal validateWithdrawal(int amountToWithdraw) {
        Withdrawal withdrawal = new Withdrawal(amountToWithdraw);
        if (amountToWithdraw <= 0 || amountToWithdraw % 100 != 0) {
            return withdrawal.decline("Invalid withdrawal amount. Must be a positive integer divisible by 100. ");
        } else if (amountToWithdraw > bank.getTotalValue()) {
            return withdrawal.decline("Funds too low. Try a lower amount. ");
        }
    
        amountToWithdraw = decideBillTypes(amountToWithdraw, withdrawal);
    
        if (amountToWithdraw != 0) {
            return withdrawal.decline("Not enough bills of the correct types to complete withdrawal. Try a different amount. ");
        } else {
            withdrawal.setApproved(true);
        }
        
        return withdrawal;
    }
    
    private int decideBillTypes(int amountToWithdraw, Withdrawal withdrawal) {
        for (Integer denomination : bank.getSortedKeysDescending()) {
            int billsOfCurrentType = Integer.min(amountToWithdraw / denomination, bank.getBillQuantityOfValue(denomination));
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
