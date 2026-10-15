import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stage 1 Warehouse facade. Public operations follow the Warehouse class box
 * in CSCI 430_class.pdf. Collections and IDs belong to the list classes.
 */
public class Warehouse {
    public Client addClient(String name, String address) {
        return ClientList.instance().insertClient(name, address);
    }

    /**
     * Entries at the same index describe one product. The three lists must
     * be non-null and have equal lengths. Product validation belongs to
     * ProductList, as specified by the design.
     */
    public List<Product> addProducts(List<String> names,
            List<Integer> amounts, List<Double> prices) {
        if (names == null || amounts == null || prices == null
                || names.size() != amounts.size()
                || names.size() != prices.size()) {
            throw new IllegalArgumentException(
                    "Product lists must be non-null and have equal lengths.");
        }

        List<Product> addedProducts = new ArrayList<>();
        Iterator<String> nameIterator = names.iterator();
        Iterator<Integer> amountIterator = amounts.iterator();
        Iterator<Double> priceIterator = prices.iterator();
        while (nameIterator.hasNext()) {
            addedProducts.add(ProductList.instance().insertProduct(
                nameIterator.next(), amountIterator.next(), priceIterator.next()));
        }
        return addedProducts;
    }

    public Client getClientByID(String clientID) {
        return ClientList.instance().search(clientID);
    }

    public Product getProductByID(String productID) {
        return ProductList.instance().search(productID);
    }

    public List<WishListItem> viewClientWishList(String clientID) {
        Client client = getClientByID(clientID);
        return client == null ? null : client.getWishList();
    }

    public WishListItem addProductToWishList(String clientID,
            String productID, int quantity) {
        Client client = getClientByID(clientID);
        Product product = getProductByID(productID);
        if (client == null || product == null) {
            return null;
        }
        return client.addToWishList(product, quantity);
    }

    public boolean placeOrder(String clientID) {
        Client client = getClientByID(clientID);
        if (client == null) {
            return false;
        }

        Iterator<WishListItem> items = new ArrayList<>(client.getWishList()).iterator();
        while (items.hasNext()) {
            WishListItem item = items.next();
            Product product = item.getProduct();
            int fulfilled = Math.min(item.getQuantity(), product.getAmountInStock());
            if (fulfilled > 0) {
                product.removeFromStock(fulfilled);
                addInvoice(client, product, fulfilled);
            }
            int unfulfilled = item.getQuantity() - fulfilled;
            if (unfulfilled > 0) {
                product.getWaitList().add(new WaitListItem(client, product, unfulfilled));
            }
            client.removeFromWishList(item);
        }
        return true;
    }

    public boolean recordPayment(String clientID, double amount) {
        Client client = getClientByID(clientID);
        if (client == null || !Double.isFinite(amount) || amount <= 0) {
            return false;
        }
        client.addTransaction(new Transaction(LocalDate.now().toString(),
                "Payment", -amount, false));
        return true;
    }

    public boolean receiveShipment(String productID, int quantity) {
        Product product = getProductByID(productID);
        if (product == null || quantity <= 0) {
            return false;
        }

        int remaining = quantity;
        Iterator<WaitListItem> waiting = product.getWaitList().iterator();
        while (waiting.hasNext() && remaining > 0) {
            WaitListItem item = waiting.next();
            int fulfilled = Math.min(remaining, item.getQuantity());
            addInvoice(item.getClient(), product, fulfilled);
            item.reduceQuantity(fulfilled);
            remaining -= fulfilled;
            if (item.getQuantity() == 0) {
                waiting.remove();
            }
        }
        product.addToStock(remaining);
        return true;
    }

    public List<WaitListItem> viewProductWaitList(String productID) {
        Product product = getProductByID(productID);
        return product == null ? null : product.getWaitList();
    }

    public List<Transaction> viewClientInvoices(String clientID) {
        Client client = getClientByID(clientID);
        return client == null ? null : client.getInvoices();
    }

    private void addInvoice(Client client, Product product, int quantity) {
        double total = quantity * product.getSalePrice();
        client.addTransaction(new Transaction(LocalDate.now().toString(),
                quantity + " x " + product.getName() + " (" + product.getId() + ")",
                total, true));
    }

    public Iterator<Client> getClients() {
        return ClientList.instance().getClients();
    }

    public Iterator<Product> getProducts() {
        return ProductList.instance().getProducts();
    }
}
