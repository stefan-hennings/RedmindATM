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
    
    public Withdrawal decline(String declineReason) {
        isApproved = false;
        this.declineReason = declineReason;
        return this;
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
            for (int value : getSortedKeysDescending()) {
                status.append(String.format("""
                        %4d: %d
                        """, value, billTypesMap.get(value)));
            }
        }
        
        return status.toString();
    }
    
    public List<Integer> getSortedKeysDescending() {
        return billTypesMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
}
