import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class TransactionService {
    private final Bank bank;
    private List<Withdrawal> withdrawalLog = new ArrayList<>();
    
    public void attemptWithdrawal(int amountToWithdraw) {
        Withdrawal withdrawal = new Withdrawal(amountToWithdraw);
        if (isValidWithdrawalAmount(withdrawal)) {
            int amountRemaining = decideBillTypes(withdrawal);
    
            if (amountRemaining != 0) {
                withdrawal.decline("Not enough bills of the correct types to complete withdrawal. Try a different amount. ");
            } else {
                withdrawal.setApproved(true);
            }
        }
        withdrawalLog.add(withdrawal);
        processWithdrawal(withdrawal);
    }
    
    public boolean isValidWithdrawalAmount(Withdrawal withdrawal) {
        if (withdrawal.getAmount() <= 0 || withdrawal.getAmount() % 100 != 0) {
            withdrawal.decline("Invalid withdrawal amount. Must be a positive integer divisible by 100. ");
            return false;
        } else if (withdrawal.getAmount() > bank.getTotalValue()) {
            withdrawal.decline("Funds too low. Try a lower amount. ");
            return false;
        }
        return true;
    }
    
    public int decideBillTypes(Withdrawal withdrawal) {
        int remainingAmountToWithdraw = withdrawal.getAmount();
        for (Integer denomination : bank.getSortedKeysDescending()) {
            int billsOfCurrentType = Integer.min(remainingAmountToWithdraw / denomination,
                    bank.getBillQuantityOfDenomination(denomination));
            remainingAmountToWithdraw -= billsOfCurrentType * denomination;
            
            withdrawal.addBillType(denomination, billsOfCurrentType);
        }
        return remainingAmountToWithdraw;
    }
    
    public void processWithdrawal(Withdrawal withdrawal) {
        if (withdrawal.isApproved()) {
            bank.removeNotes(withdrawal);
        }
        System.out.println(withdrawal);
    }
}
