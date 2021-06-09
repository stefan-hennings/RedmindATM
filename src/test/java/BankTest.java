import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class BankTest {
    private Bank bankMocked;
    
    @BeforeEach
    void init() {
        Map<Integer, Integer> availableBillsMapMocked = new HashMap<>();
        availableBillsMapMocked.put(500, 3);
        availableBillsMapMocked.put(1000, 2);
        availableBillsMapMocked.put(100, 5);
        bankMocked = new Bank(availableBillsMapMocked);
    }
    
    @Test
    void getTotalValueTest() {
        assertEquals(bankMocked.getTotalValue(), 4000);
    }
    
    @Test
    void removeNotesTest() {
        Withdrawal withdrawalMocked = new Withdrawal(1700);
        withdrawalMocked.getBillTypesMap().put(1000, 1);
        withdrawalMocked.getBillTypesMap().put(500, 1);
        withdrawalMocked.getBillTypesMap().put(100, 2);
        
        bankMocked.removeNotes(withdrawalMocked);
        assertEquals(bankMocked.getAvailableBillsMap().get(1000), 1);
        assertEquals(bankMocked.getAvailableBillsMap().get(500), 2);
        assertEquals(bankMocked.getAvailableBillsMap().get(100), 3);
    }
    
    @Test
    void getSortedKeysDescendingTest() {
        List<Integer> result = bankMocked.getSortedKeysDescending();
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i) < result.get(i - 1));
        }
    }
}