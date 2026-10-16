import java.io.Serializable;

public class WaitListItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Client client;
    private final Product product;
    private final Order order;
    private int quantity;

    public WaitListItem(Client client, Product product, int quantity) {
        this(client, product, quantity, null);
    }

    public WaitListItem(Client client, Product product,
            int quantity, Order order) {
        if (client == null || product == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Client and product are required; quantity must be positive.");
        }

        if (order != null
                && !client.getId().equals(order.getClientId())) {
            throw new IllegalArgumentException(
                    "The order must belong to the specified client.");
        }

        this.client = client;
        this.product = product;
        this.quantity = quantity;
        this.order = order;
    }

    public Client getClient() {
        return client;
    }

    public Product getProduct() {
        return product;
    }

    public Order getOrder() {
        return order;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int quantity) {
        if (quantity <= 0 || quantity > this.quantity) {
            throw new IllegalArgumentException(
                    "Reduction must be positive and cannot exceed outstanding quantity.");
        }

        this.quantity -= quantity;
    }
}