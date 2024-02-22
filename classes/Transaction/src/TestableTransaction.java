import java.text.DecimalFormat;
import java.util.Objects;

public class TestableTransaction extends Transaction {

    public TestableTransaction(String owner, String date, float amount) {
        super(owner, date, amount);
    }