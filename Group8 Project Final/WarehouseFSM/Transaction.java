import java.io.*;
import java.security.SecureRandom;
import java.util.Locale;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final SecureRandom rand = new SecureRandom();
    private final long transactionId = Math.abs(rand.nextLong());
    private double amount;
    private String date;
    private String description;
    private boolean invoice;

    public Transaction(double amount) {
        this(null, "Purchase", amount, true);
    }

    public Transaction(String date, double amount) {
        this(date, "Purchase", amount, true);
    }

    public Transaction(String date, String description, double amount,
            boolean invoice) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.invoice = invoice;
    }

    public String getDate() {
        return date;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInvoice() {
        return invoice;
    }

    public void Invoice() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        String heading = invoice ? "Invoice" : "Transaction";
        String amountLabel = invoice ? "Invoice total" : "Amount";

        return String.format(
                Locale.US,
                "%s ID: %d%nDate: %s%nDescription: %s%n%s: $%.2f",
                heading,
                transactionId,
                date,
                description,
                amountLabel,
                amount);
    }
}