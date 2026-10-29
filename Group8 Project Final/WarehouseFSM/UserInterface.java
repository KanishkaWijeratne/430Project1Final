import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public class UserInterface {
    private final BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
    private final Warehouse warehouse;

    UserInterface(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getToken(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String line = reader.readLine();
                if (line == null) {
                    throw new NoSuchElementException("End of input");
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    return line;
                }
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
    }

    public int getNumber(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(getToken(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Please input a whole number");
            }
        }
    }

    private int getQuantity(String prompt, int minimum) {
        while (true) {
            int quantity = getNumber(prompt);
            if (quantity >= minimum) {
                return quantity;
            }
            System.out.println("Please input a quantity of at least " + minimum);
        }
    }

    private double getAmount(String prompt) {
        while (true) {
            try {
                double amount = Double.parseDouble(getToken(prompt));
                if (Double.isFinite(amount) && amount > 0) {
                    return amount;
                }
            } catch (NumberFormatException exception) {
                // Continue prompting until a valid amount is entered.
            }
            System.out.println("Please input a positive, finite amount");
        }
    }

    private boolean yesOrNo(String prompt) {
        String answer = getToken(prompt + " (Y/n)");
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    public void addClient() {
        String name = getToken("Enter client name");
        String address = getToken("Enter address");
        Client client = warehouse.addClient(name, address);
        System.out.println(client == null ? "Client could not be added" : client);
    }

    public void addProducts() {
        do {
            String name = getToken("Enter product name");
            int quantity = getQuantity("Enter initial quantity in stock", 0);
            double price = getAmount("Enter unit sale price");
            List<Product> products = warehouse.addProducts(
                    java.util.Collections.singletonList(name),
                    java.util.Collections.singletonList(quantity),
                    java.util.Collections.singletonList(price));
            if (products.isEmpty() || products.get(0) == null) {
                System.out.println("Product could not be added");
            } else {
                System.out.println(products.get(0));
            }
        } while (yesOrNo("Add another product?"));
    }

    public void showClients() {
        Iterator<Client> clients = warehouse.getClients();
        if (!clients.hasNext()) {
            System.out.println("There are no clients");
            return;
        }
        while (clients.hasNext()) {
            Client client = clients.next();
            double balance = client.getBalance();
            String status = balance > 0 ? "debit" : balance < 0 ? "credit" : "zero";
            System.out.printf(Locale.US, "%s Balance: $%.2f (%s)%n",
                    client, Math.abs(balance), status);
        }
    }

    public void showProducts(boolean showQuantity) {
        Iterator<Product> products = warehouse.getProducts();
        if (!products.hasNext()) {
            System.out.println("There are no products");
            return;
        }
        if (showQuantity) {
            System.out.printf("%-8s %-24s %12s %12s%n",
                    "ID", "Product", "Unit price", "Available");
        } else {
            System.out.printf("%-8s %-24s %12s%n", "ID", "Product", "Unit price");
        }
        while (products.hasNext()) {
            Product product = products.next();
            if (showQuantity) {
                System.out.printf(Locale.US, "%-8s %-24s $%11.2f %12d%n",
                        product.getId(), product.getName(), product.getSalePrice(),
                        product.getAmountInStock());
            } else {
                System.out.printf(Locale.US, "%-8s %-24s $%11.2f%n",
                        product.getId(), product.getName(), product.getSalePrice());
            }
        }
    }

    public void showOutstandingClients() {
        Iterator<Client> clients = warehouse.getClientsWithOutstandingBalance();
        if (!clients.hasNext()) {
            System.out.println("No clients have an outstanding balance");
            return;
        }
        System.out.printf("%-8s %-24s %-24s %14s%n",
                "ID", "Name", "Address", "Amount owed");
        while (clients.hasNext()) {
            Client client = clients.next();
            System.out.printf(Locale.US, "%-8s %-24s %-24s $%13.2f%n",
                    client.getId(), client.getName(), client.getAddress(),
                    client.getBalance());
        }
    }

    public void showClientDetails(String clientID) {
        Client client = warehouse.getClientByID(clientID);
        if (client == null) {
            System.out.println("No such client");
            return;
        }
        double balance = client.getBalance();
        String status = balance > 0 ? "debit" : balance < 0 ? "credit" : "zero";
        System.out.printf(Locale.US, "%s%nAddress: %s%nBalance: $%.2f (%s)%n",
                client, client.getAddress(), Math.abs(balance), status);
    }

    public void showTransactions(String clientID) {
        Iterator<Transaction> transactions = warehouse.viewClientTransactions(clientID);
        if (transactions == null) {
            System.out.println("No such client");
            return;
        }
        if (!transactions.hasNext()) {
            System.out.println("There are no transactions for this client");
            return;
        }
        Client client = warehouse.getClientByID(clientID);
        System.out.printf("Transactions for %s - %s%n", client.getId(), client.getName());
        while (transactions.hasNext()) {
            Transaction transaction = transactions.next();
            System.out.printf(Locale.US, "%s | %s | %s | $%+.2f%n",
                    transaction.getTransactionId(), transaction.getDate(),
                    transaction.getDescription(), transaction.getAmount());
        }
        double balance = client.getBalance();
        System.out.printf(Locale.US, "Balance: $%.2f (%s)%n", Math.abs(balance),
                balance > 0 ? "owed" : balance < 0 ? "credit" : "zero");
    }

    public void addToWishList(String clientID) {
        do {
            String productID = getToken("Enter product id");
            if (warehouse.getProductByID(productID) == null) {
                System.out.println("No such product");
            } else {
                int quantity = getQuantity("Enter desired quantity", 1);
                WishListItem item = warehouse.addProductToWishList(
                        clientID, productID, quantity);
                System.out.println(item == null ? "Product could not be added to wishlist"
                        : "Wishlist updated: " + item.getProduct().getId()
                                + " quantity " + item.getQuantity());
            }
        } while (yesOrNo("Add another product to this wishlist?"));
    }

    public void showWishList(String clientID) {
        List<WishListItem> items = warehouse.viewClientWishList(clientID);
        if (items == null) {
            System.out.println("No such client");
            return;
        }
        if (items.isEmpty()) {
            System.out.println("Wishlist is empty");
            return;
        }
        for (WishListItem item : items) {
            Product product = item.getProduct();
            System.out.printf("%s %s quantity %d%n", product.getId(),
                    product.getName(), item.getQuantity());
        }
    }

    public void placeOrder(String clientID) {
        Order order = warehouse.placeOrder(clientID);
        if (order == null) {
            System.out.println("No order placed; wishlist is empty");
            return;
        }
        System.out.printf(Locale.US,
                "Order %s placed for %s on %s. Amount charged: $%.2f%n",
                order.getId(), order.getClientId(), order.getDate(),
                order.getTotalCharged());
        System.out.println("Any unfulfilled quantities are on the products' waitlists.");
    }

    public void showWaitList() {
        String productID = getToken("Enter product id");
        List<WaitListItem> items = warehouse.viewProductWaitList(productID);
        if (items == null) {
            System.out.println("No such product");
            return;
        }
        if (items.isEmpty()) {
            System.out.println("Waitlist is empty");
            return;
        }
        for (WaitListItem item : items) {
            System.out.printf("Client %s %s product %s quantity %d%n",
                    item.getClient().getId(), item.getClient().getName(),
                    item.getProduct().getId(), item.getQuantity());
        }
    }

    public void recordPayment() {
        String clientID = getToken("Enter client id");
        if (warehouse.getClientByID(clientID) == null) {
            System.out.println("No such client");
            return;
        }
        double amount = getAmount("Enter payment amount");
        System.out.println(warehouse.recordPayment(clientID, amount)
                ? "Payment recorded" : "Payment could not be recorded");
    }

    public void receiveShipment() {
        String productID = getToken("Enter product id");
        if (warehouse.getProductByID(productID) == null) {
            System.out.println("No such product");
            return;
        }
        int quantity = getQuantity("Enter shipment quantity", 1);
        try {
            System.out.println(warehouse.receiveShipment(productID, quantity)
                    ? "Shipment received; waitlist items filled first and remaining units added to stock."
                    : "Shipment could not be received");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            WarehouseContext.instance().process();
        } catch (NoSuchElementException exception) {
            System.out.println("End of input; exiting.");
        } catch (UncheckedIOException exception) {
            System.err.println("Could not read input: " + exception.getCause().getMessage());
        }
    }
}
