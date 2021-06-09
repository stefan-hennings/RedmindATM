import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Bank {
    private Map<Integer, Integer> availableBillsMap;
    
    public int getTotalValue() {
        int totalValue = 0;
        for (int denomination : availableBillsMap.keySet()) {
            totalValue += denomination * availableBillsMap.get(denomination);
        }
        return totalValue;
    }
    
    public void removeNotes(Withdrawal withdrawal) {
        Map<Integer, Integer> billsToRemove = withdrawal.getBillTypesMap();
        for (Integer denomination : billsToRemove.keySet()) {
            int newQuantity = availableBillsMap.get(denomination) - billsToRemove.get(denomination);
            availableBillsMap.put(denomination, newQuantity);
        }
    }
    
    public int getBillQuantityOfDenomination(int billDenomination) {
        return availableBillsMap.get(billDenomination);
    }
    
    public List<Integer> getSortedKeysDescending() {
        return availableBillsMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
}
