import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WithdrawalTest {
    private Withdrawal withdrawalMocked;
    
    @BeforeEach
    void init() {
        withdrawalMocked = new Withdrawal(1700);
        withdrawalMocked.addBillType(500, 1);
        withdrawalMocked.addBillType(1000, 1);
        withdrawalMocked.addBillType(100, 2);
    }
    
    @Test
    void addBillTypeTest() {
        assertEquals(withdrawalMocked.getBillTypesMap().get(1000), 1);
        assertNotEquals(withdrawalMocked.getBillTypesMap().get(1000), 2);
        assertEquals(withdrawalMocked.getBillTypesMap().get(500), 1);
        assertNotEquals(withdrawalMocked.getBillTypesMap().get(500), 2);
        assertEquals(withdrawalMocked.getBillTypesMap().get(100), 2);
        assertNotEquals(withdrawalMocked.getBillTypesMap().get(100), 3);
    }
    
    @Test
    void declineTest() {
        String declineReason = "Test reason";
        withdrawalMocked.decline(declineReason);
        
        assertFalse(withdrawalMocked.isApproved());
        assertEquals(withdrawalMocked.getDeclineReason(), declineReason);
        assertNotEquals(withdrawalMocked.getDeclineReason(), "Test");
    }
    
    @Test
    void getSortedKeysDescending() {
        List<Integer> result = withdrawalMocked.getSortedKeysDescending();
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i) < result.get(i - 1));
        }
    }
}
