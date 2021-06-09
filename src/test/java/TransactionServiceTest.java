import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    private TransactionService transactionServiceMocked;
    
    @BeforeEach
    void init() {
        Map<Integer, Integer> availableBillsMapMocked = new HashMap<>();
        availableBillsMapMocked.put(500, 3);
        availableBillsMapMocked.put(1000, 2);
        availableBillsMapMocked.put(100, 5);
        Bank bankMocked = new Bank(availableBillsMapMocked);
        transactionServiceMocked = new TransactionService(bankMocked);
    }
    
    @Test
    void attemptWithdrawalSuccessfulTest() {
        transactionServiceMocked.attemptWithdrawal(1700);
        
        Map<Integer, Integer> remainingBillsMap = transactionServiceMocked.getBank().getAvailableBillsMap();
        List<Withdrawal> withdrawalLog = transactionServiceMocked.getWithdrawalLog();
        
        assertTrue(withdrawalLog.get(withdrawalLog.size() - 1).isApproved());
        
        assertEquals(remainingBillsMap.get(1000), 1);
        assertNotEquals(remainingBillsMap.get(1000), 2);
        assertEquals(remainingBillsMap.get(500), 2);
        assertNotEquals(remainingBillsMap.get(500), 3);
        assertEquals(remainingBillsMap.get(100), 3);
        assertNotEquals(remainingBillsMap.get(100), 5);
    }
    
    @Test
    void attemptWithdrawalInsufficientFundsTest() {
        transactionServiceMocked.attemptWithdrawal(4100);
        
        List<Withdrawal> withdrawalLog = transactionServiceMocked.getWithdrawalLog();
        Withdrawal withdrawal = withdrawalLog.get(withdrawalLog.size() - 1);
        
        assertEquals(withdrawal.getDeclineReason(), "Funds too low. Try a lower amount. ");
        
        assertNoBillsRemovedAndNotApproved(withdrawal);
    }
    
    private void assertNoBillsRemovedAndNotApproved(Withdrawal withdrawal) {
        Map<Integer, Integer> remainingBillsMap = transactionServiceMocked.getBank().getAvailableBillsMap();
        assertFalse(withdrawal.isApproved());
        assertNotEquals(remainingBillsMap.get(1000), 1);
        assertEquals(remainingBillsMap.get(1000), 2);
        assertNotEquals(remainingBillsMap.get(500), 2);
        assertEquals(remainingBillsMap.get(500), 3);
        assertNotEquals(remainingBillsMap.get(100), 3);
        assertEquals(remainingBillsMap.get(100), 5);
    }
    
    @Test
    void attemptWithdrawalNegativeNumberTest() {
        transactionServiceMocked.attemptWithdrawal(-100);
        
        List<Withdrawal> withdrawalLog = transactionServiceMocked.getWithdrawalLog();
        Withdrawal withdrawal = withdrawalLog.get(withdrawalLog.size() - 1);
        assertNoBillsRemovedAndNotApproved(withdrawal);
        
        assertEquals(withdrawal.getDeclineReason(), "Invalid withdrawal amount. Must be a positive integer divisible by 100. ");
    }
    
    @Test
    void attemptWithdrawalNotDivisibleBy100Test() {
        transactionServiceMocked.attemptWithdrawal(99);
        
        List<Withdrawal> withdrawalLog = transactionServiceMocked.getWithdrawalLog();
        Withdrawal withdrawal = withdrawalLog.get(withdrawalLog.size() - 1);
        assertNoBillsRemovedAndNotApproved(withdrawal);
        
        assertEquals(withdrawal.getDeclineReason(), "Invalid withdrawal amount. Must be a positive integer divisible by 100. ");
    }
    
    @Test
    void attemptWithdrawalInsufficientBillsTest() {
        transactionServiceMocked.getBank().getAvailableBillsMap().put(100, 3);
        transactionServiceMocked.attemptWithdrawal(900);
        
        List<Withdrawal> withdrawalLog = transactionServiceMocked.getWithdrawalLog();
        Withdrawal withdrawal = withdrawalLog.get(withdrawalLog.size() - 1);
        
        assertEquals(withdrawal.getDeclineReason(), "Not enough bills of the correct types to complete withdrawal. Try a different amount. ");
        
        Map<Integer, Integer> remainingBillsMap = transactionServiceMocked.getBank().getAvailableBillsMap();
        assertFalse(withdrawal.isApproved());
        assertNotEquals(remainingBillsMap.get(1000), 1);
        assertEquals(remainingBillsMap.get(1000), 2);
        assertNotEquals(remainingBillsMap.get(500), 2);
        assertEquals(remainingBillsMap.get(500), 3);
        assertNotEquals(remainingBillsMap.get(100), 5);
        assertEquals(remainingBillsMap.get(100), 3);
    }
    
    @Test
    void decideBillTypesTest() {
        Withdrawal withdrawal = new Withdrawal(800);
        assertEquals(transactionServiceMocked.decideBillTypes(withdrawal), 0);
        Map<Integer, Integer> billsUsedMap = withdrawal.getBillTypesMap();
        assertEquals(billsUsedMap.get(1000), 0);
        assertNotEquals(billsUsedMap.get(1000), 1);
        assertEquals(billsUsedMap.get(500), 1);
        assertNotEquals(billsUsedMap.get(500), 0);
        assertEquals(billsUsedMap.get(100), 3);
        assertNotEquals(billsUsedMap.get(100), 4);
    
        withdrawal = new Withdrawal(3700);
        assertEquals(transactionServiceMocked.decideBillTypes(withdrawal), 0);
        billsUsedMap = withdrawal.getBillTypesMap();
        assertEquals(billsUsedMap.get(1000), 2);
        assertNotEquals(billsUsedMap.get(1000), 1);
        assertEquals(billsUsedMap.get(500), 3);
        assertNotEquals(billsUsedMap.get(500), 0);
        assertEquals(billsUsedMap.get(100), 2);
        assertNotEquals(billsUsedMap.get(100), 4);
    
        
        transactionServiceMocked.getBank().getAvailableBillsMap().put(100, 3);
        withdrawal = new Withdrawal(400);
        assertEquals(transactionServiceMocked.decideBillTypes(withdrawal), 100);
        billsUsedMap = withdrawal.getBillTypesMap();
        assertEquals(billsUsedMap.get(1000), 0);
        assertNotEquals(billsUsedMap.get(1000), 1);
        assertEquals(billsUsedMap.get(500), 0);
        assertNotEquals(billsUsedMap.get(500), 1);
        assertEquals(billsUsedMap.get(100), 3);
        assertNotEquals(billsUsedMap.get(100), 4);
    }
    
    @Test
    void isNotValidWithdrawalAmountTest() {
        Withdrawal withdrawal = new Withdrawal(99);
        assertFalse(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(0);
        assertFalse(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(-200);
        assertFalse(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(-50);
        assertFalse(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(1);
        assertFalse(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(transactionServiceMocked.getBank().getTotalValue() + 1);
        assertFalse(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
    }
    
    @Test
    void isValidWithdrawalAmountTest() {
        Withdrawal withdrawal = new Withdrawal(100);
        assertTrue(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(500);
        assertTrue(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(4000);
        assertTrue(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(1700);
        assertTrue(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
        withdrawal = new Withdrawal(800);
        assertTrue(transactionServiceMocked.isValidWithdrawalAmount(withdrawal));
    }
    
    @Test
    void removeNotesTest() {
        Withdrawal withdrawalMocked = new Withdrawal(1700);
        withdrawalMocked.getBillTypesMap().put(1000, 1);
        withdrawalMocked.getBillTypesMap().put(500, 1);
        withdrawalMocked.getBillTypesMap().put(100, 2);
    
        Bank bank = transactionServiceMocked.getBank();
    
        bank.removeNotes(withdrawalMocked);
        assertEquals(bank.getAvailableBillsMap().get(1000), 1);
        assertNotEquals(bank.getAvailableBillsMap().get(1000), 2);
        assertEquals(bank.getAvailableBillsMap().get(500), 2);
        assertNotEquals(bank.getAvailableBillsMap().get(500), 3);
        assertEquals(bank.getAvailableBillsMap().get(100), 3);
        assertNotEquals(bank.getAvailableBillsMap().get(100), 5);
    }
}
