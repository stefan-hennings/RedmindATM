import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class Withdrawal {
    private final int amount;
    private boolean isApproved;
    private String declineReason;
    private final Map<Integer, Integer> billTypesMap = new HashMap<>();
    
    public void addBillType(int denomination, int quantity) {
        billTypesMap.put(denomination, quantity);
    }
    
    public void decline(String declineReason) {
        isApproved = false;
        this.declineReason = declineReason;
    }
    
    public List<Integer> getSortedKeysDescending() {
        return billTypesMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        StringBuilder status = new StringBuilder(String.format("""
                Amount: %d
                Approved: %b
                """, amount, isApproved));
        if (!isApproved) {
            status.append(String.format("Reason: %s\n", declineReason));
        } else {
            status.append("Bills used: \n");
            for (int denomination : getSortedKeysDescending()) {
                status.append(String.format("""
                        %4d: %d
                        """, denomination, billTypesMap.get(denomination)));
            }
        }
        
        return status.toString();
    }
}
