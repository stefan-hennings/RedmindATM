import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillType implements Comparable<BillType>{
    private final int value;
    private int quantity;
    
    @Override
    public int compareTo(BillType otherBill) {
        return Integer.compare(value, otherBill.getValue());
    }
}
