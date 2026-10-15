import java.io.Serializable;

/** A product quantity reserved for a client until stock arrives. */
public class WaitListItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Client client;
    private final Product product;
    private int quantity;

    public WaitListItem(Client client, Product product, int quantity) {
        if (client == null || product == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Client and product are required and quantity must be positive.");
        }
        this.client = client;
        this.product = product;
        this.quantity = quantity;
    }

    public Client getClient() {
        return client;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int quantity) {
        if (quantity <= 0 || quantity > this.quantity) {
            throw new IllegalArgumentException("Invalid waitlist quantity.");
        }
        this.quantity -= quantity;
    }
}