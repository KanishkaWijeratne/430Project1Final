import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter; 
    private final String id;
    private final String clientId;
    private final String date;
    private final List<WishListItem> items;
    private double totalCharged;

    /** items should be the copy returned by Client.getWishList(). */
    public Order(String clientId, List<WishListItem> items) {
        this.clientId = clientId;
        this.items = new ArrayList<>(items);
        this.date = LocalDate.now().toString();
        this.id = String.format("%s-%04d", date, ++idCounter);
    }

    /** Called by Warehouse for each line where units were supplied. */
    public void addCharge(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            throw new IllegalArgumentException("Amount must be positive and finite.");
        }
        totalCharged += amount;
    }

    public Iterator<WishListItem> getItems() {
       return Collections.unmodifiableList(items).iterator();
    }

    public double getTotalCharged() {
        return totalCharged;
    }
    
    public String getId() {
        return id;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getDate() {
        return date;
    }
            
}