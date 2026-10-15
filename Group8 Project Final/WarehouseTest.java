import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class WarehouseTest {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        System.out.println("WAREHOUSE TEST WALKTHROUGH");

        section("Create five clients");
        for (int i = 1; i <= 5; i++) {
            warehouse.addClient("Client " + i, "Address " + i);
        }
        printClients(warehouse);

        section("Create five products");
        warehouse.addProducts(Arrays.asList("Product 1", "Product 2", "Product 3",
                        "Product 4", "Product 5"),
                Arrays.asList(10, 20, 30, 40, 50),
                Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        printProducts(warehouse);

        section("C1 wishlist: 5 each of P1, P3, P5");
        addItems(warehouse, "C1", new String[] {"P1", "P3", "P5"}, 5);
        printWishlist(warehouse, "C1");

        section("C2 wishlist: 7 each of P1, P2, P4");
        addItems(warehouse, "C2", new String[] {"P1", "P2", "P4"}, 7);
        printWishlist(warehouse, "C2");

        section("C3 wishlist: 6 each of P1, P2, P5");
        addItems(warehouse, "C3", new String[] {"P1", "P2", "P5"}, 6);
        printWishlist(warehouse, "C3");

        section("Place C2 order");
        require(warehouse.placeOrder("C2"), "C2 order was not accepted");
        printClients(warehouse);

        section("Place C3 order");
        require(warehouse.placeOrder("C3"), "C3 order was not accepted");
        printClients(warehouse);
        printWishlist(warehouse, "C2");
        printWishlist(warehouse, "C3");
        printWaitList(warehouse, "P1");
        printWaitList(warehouse, "P2");
        require(warehouse.viewProductWaitList("P1").size() == 1
            && warehouse.viewProductWaitList("P1").get(0).getQuantity() == 3,
            "C3's unfulfilled P1 quantity should be waitlisted");

        section("Place C1 order");
        require(warehouse.placeOrder("C1"), "C1 order was not accepted");
        printClients(warehouse);
        printWishlist(warehouse, "C1");
        printWaitList(warehouse, "P1");

        section("Record C1 and C2 payments");
        require(warehouse.recordPayment("C1", 100), "C1 payment failed");
        require(warehouse.recordPayment("C2", 100), "C2 payment failed");
        printClients(warehouse);

        section("Receive 100 P1 units");
        require(warehouse.receiveShipment("P1", 100), "P1 shipment failed");
        printProducts(warehouse);
        printClients(warehouse);
        printInvoices(warehouse, "C1");
        printInvoices(warehouse, "C2");

        require(near(warehouse.getClientByID("C1").getBalance(), -55.0),
            "C1 final balance should include the filled waitlist invoice");
        require(near(warehouse.getClientByID("C2").getBalance(), -51.0),
            "C2 final balance is incorrect");
        require(near(warehouse.getClientByID("C3").getBalance(), 48.0),
            "C3 final balance should include the filled waitlist invoice");
        require(warehouse.getProductByID("P1").getAmountInStock() == 92,
            "P1 should have 92 units after filling both waitlist entries");
        require(warehouse.viewProductWaitList("P1").isEmpty(),
            "P1 waitlist should be empty after shipment");
        require(warehouse.viewClientInvoices("C1").size() == 3,
            "C1 should have three invoices including waitlist fulfillment");
        require(warehouse.viewClientInvoices("C2").size() == 3,
            "C2 should have three product invoices");
        System.out.println("All warehouse behavior checks passed.");

    }

    private static void printClients(Warehouse warehouse) {
        System.out.printf("%-4s | %-12s | %-12s | %-12s%n",
            "ID", "Name", "Address", "Balance");
        Iterator<Client> clients = warehouse.getClients();
        while (clients.hasNext()) {
            Client client = clients.next();
            System.out.printf(Locale.US, "%-4s | %-12s | %-12s | $%-11.2f%n",
                client.getId(), client.getName(), client.getAddress(), client.getBalance());
        }
    }

    private static void printProducts(Warehouse warehouse) {
        System.out.printf("%-4s | %-12s | %-8s | %-10s%n", "ID", "Name", "Quantity", "Unit price");
        Iterator<Product> products = warehouse.getProducts();
        while (products.hasNext()) {
            Product product = products.next();
            System.out.printf(Locale.US, "%-4s | %-12s | %-8d | $%-9.2f%n",
                    product.getId(), product.getName(), product.getAmountInStock(),
                    product.getSalePrice());
        }
    }

    private static void addItems(Warehouse warehouse, String clientId,
            String[] productIds, int quantity) {
        Iterator<String> products = Arrays.asList(productIds).iterator();
        while (products.hasNext()) {
            String productId = products.next();
            warehouse.addProductToWishList(clientId, productId, quantity);
        }
    }

    private static void printWishlist(Warehouse warehouse, String clientId) {
        System.out.println(clientId + " wishlist");
        System.out.printf("%-4s | %-12s | %-8s%n", "ID", "Product", "Quantity");
        Iterator<WishListItem> items = warehouse.viewClientWishList(clientId).iterator();
        while (items.hasNext()) {
            WishListItem item = items.next();
            Product product = item.getProduct();
            System.out.printf("%-4s | %-12s | %-8d%n", product.getId(),
                    product.getName(), item.getQuantity());
        }
    }

    private static void printWaitList(Warehouse warehouse, String productId) {
        System.out.println(productId + " waitlist");
        Iterator<WaitListItem> items = warehouse.viewProductWaitList(productId).iterator();
        while (items.hasNext()) {
            WaitListItem item = items.next();
            System.out.printf("%s | quantity %d%n", item.getClient().getId(),
                    item.getQuantity());
        }
    }

    private static void printInvoices(Warehouse warehouse, String clientId) {
        System.out.println(clientId + " invoices");
        Iterator<Transaction> invoices = warehouse.viewClientInvoices(clientId).iterator();
        while (invoices.hasNext()) {
            Transaction invoice = invoices.next();
            invoice.Invoice();
        }
    }

    private static boolean near(double actual, double expected) {
        return Math.abs(actual - expected) < 0.000001;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void section(String title) {
        System.out.println("\n--- " + title + " ---");
    }

}
