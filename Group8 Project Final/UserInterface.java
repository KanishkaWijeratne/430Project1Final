import java.util.*;
import java.io.*;

/** Console interface for Group8 Project Final, in the LibraryFinal style. */
public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;
  private static final int ADD_PRODUCTS = 2;
  private static final int SHOW_CLIENTS = 3;
  private static final int SHOW_PRODUCTS = 4;
  private static final int ADD_TO_WISHLIST = 5;
  private static final int SHOW_WISHLIST = 6;
  private static final int PLACE_ORDER = 7;
  private static final int SHOW_WAITLIST = 8;
  private static final int RECORD_PAYMENT = 9;
  private static final int RECEIVE_SHIPMENT = 10;
  private static final int SHOW_INVOICES = 11;
  private static final int SHOW_TRANSACTIONS = 12;
  private static final int SHOW_OUTSTANDING_CLIENTS = 13;
  private static final int HELP = 14;

  private UserInterface() {
    warehouse = new Warehouse();
  }

  public static UserInterface instance() {
    if (userInterface == null) {
      return userInterface = new UserInterface();
    } else {
      return userInterface;
    }
  }

  public String getToken(String prompt) {
    do {
      try {
        System.out.println(prompt);
        String line = reader.readLine();
        if (line == null) {
          throw new NoSuchElementException("End of input");
        }
        StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
        if (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken().trim();
          if (!token.isEmpty()) {
            return token;
          }
        }
      } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
      }
    } while (true);
  }

  private boolean yesOrNo(String prompt) {
    String more = getToken(prompt + " (Y|y)[es] or anything else for no");
    return more.charAt(0) == 'y' || more.charAt(0) == 'Y';
  }

  public int getNumber(String prompt) {
    do {
      try {
        return Integer.parseInt(getToken(prompt));
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a whole number");
      }
    } while (true);
  }

  private int getQuantity(String prompt, int minimum) {
    do {
      int quantity = getNumber(prompt);
      if (quantity >= minimum) {
        return quantity;
      }
      System.out.println("Please input a quantity of at least " + minimum);
    } while (true);
  }

  public double getAmount(String prompt) {
    do {
      try {
        double amount = Double.parseDouble(getToken(prompt));
        if (Double.isFinite(amount) && amount > 0) {
          return amount;
        }
      } catch (NumberFormatException nfe) {
        // Repeat the prompt for invalid monetary input.
      }
      System.out.println("Please input a positive, finite amount");
    } while (true);
  }

  public int getCommand() {
    do {
      int value = getNumber(
          "Enter command: " + HELP + " for help");

      if (value >= EXIT && value <= HELP) {
        return value;
      }

      System.out.println(
          "Enter a number between " + EXIT + " and " + HELP);
    } while (true);
  }

  public void help() {
    System.out.println(
        "Enter a number between 0 and " + HELP
            + " as explained below:");

    System.out.println(EXIT + " to Exit");
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(ADD_PRODUCTS + " to add products");
    System.out.println(
        SHOW_CLIENTS + " to print all clients and balances");
    System.out.println(
        SHOW_PRODUCTS + " to print all products");
    System.out.println(
        ADD_TO_WISHLIST + " to add products to a client's wishlist");
    System.out.println(
        SHOW_WISHLIST + " to print a client's wishlist");
    System.out.println(
        PLACE_ORDER + " to order everything in a client's wishlist");
    System.out.println(
        SHOW_WAITLIST + " to print a product's waitlist");
    System.out.println(
        RECORD_PAYMENT + " to record a client's payment");
    System.out.println(
        RECEIVE_SHIPMENT + " to receive a product shipment");
    System.out.println(
        SHOW_INVOICES + " to print all invoices for a client");
    System.out.println(
        SHOW_TRANSACTIONS + " to print all transactions for a client");
    System.out.println(
        SHOW_OUTSTANDING_CLIENTS
            + " to print clients with outstanding balances");
    System.out.println(HELP + " for help");
  }

  public void addClient() {
    String name = getToken("Enter client name");
    String address = getToken("Enter address");
    Client result = warehouse.addClient(name, address);
    if (result != null) {
      System.out.println(result);
    } else {
      System.out.println("Client could not be added");
    }
  }

  public void addProducts() {
    do {
      String name = getToken("Enter product name");
      int quantity = getQuantity("Enter initial quantity in stock", 0);
      double price = getAmount("Enter unit sale price");
      List<Product> result = warehouse.addProducts(
          Collections.singletonList(name), Collections.singletonList(quantity),
          Collections.singletonList(price));
      if (result == null || result.isEmpty()) {
        System.out.println("Product could not be added");
      } else {
        Iterator<Product> products = result.iterator();
        while (products.hasNext()) {
          Product product = products.next();
          System.out.println(product == null ? "Product could not be added" : product);
        }
      }
      if (!yesOrNo("Add more products?")) {
        break;
      }
    } while (true);
  }

  public void showClients() {
    Iterator<Client> clients = warehouse.getClients();
    if (!clients.hasNext()) {
      System.out.println("There are no clients");
    }
    while (clients.hasNext()) {
      Client client = clients.next();
      double balance = client.getBalance();
      System.out.printf(Locale.US, "%s Balance: $%.2f (%s)%n", client,
          Math.abs(balance), balance > 0 ? "debit" : balance < 0 ? "credit" : "zero");
    }
  }

  public void showProducts() {
    Iterator<Product> products = warehouse.getProducts();

    if (!products.hasNext()) {
      System.out.println("There are no products");
      return;
    }

    System.out.printf(
        "%-8s %-24s %12s %12s %14s%n",
        "ID", "Product", "Unit price", "In stock", "Waitlisted");

    while (products.hasNext()) {
      Product product = products.next();

      System.out.printf(
          Locale.US,
          "%-8s %-24s $%11.2f %12d %14d%n",
          product.getId(),
          product.getName(),
          product.getSalePrice(),
          product.getAmountInStock(),
          product.getTotalWaitlistedQuantity());
    }
  }

  public void addToWishList() {
    String clientID = getToken("Enter client id");
    if (warehouse.getClientByID(clientID) == null) {
      System.out.println("No such client");
      return;
    }
    do {
      String productID = getToken("Enter product id");
      if (warehouse.getProductByID(productID) == null) {
        System.out.println("No such product");
      } else {
        int quantity = getQuantity("Enter desired quantity (replaces any existing wishlist quantity)", 1);
        WishListItem result = warehouse.addProductToWishList(clientID, productID, quantity);
        if (result != null) {
          System.out.println("Wishlist updated: " + result.getProduct().getId()
              + " quantity " + result.getQuantity());
        } else {
          System.out.println("Product could not be added to wishlist");
        }
      }
      if (!yesOrNo("Add more products to this wishlist?")) {
        break;
      }
    } while (true);
  }

  public void showWishList() {
    String clientID = getToken("Enter client id");
    List<WishListItem> result = warehouse.viewClientWishList(clientID);
    if (result == null) {
      System.out.println("No such client");
      return;
    }
    if (result.isEmpty()) {
      System.out.println("Wishlist is empty");
    }
    Iterator<WishListItem> items = result.iterator();
    while (items.hasNext()) {
      WishListItem item = items.next();
      System.out.println(item.getProduct().getId() + " " + item.getProduct().getName()
          + " quantity " + item.getQuantity());
    }
  }

  public void placeOrder() {
    String clientID = getToken("Enter client id");
    if (warehouse.getClientByID(clientID) == null) {
      System.out.println("No such client");
      return;
    }
    Order result = warehouse.placeOrder(clientID);
    if (result == null) {
      System.out.println("No order placed; wishlist is empty");
    } else {
      System.out.printf(Locale.US, "Order %s placed for %s on %s. Amount charged: $%.2f%n",
          result.getId(), result.getClientId(), result.getDate(), result.getTotalCharged());
      System.out.println("Any unfulfilled quantities are on the products' waitlists.");
    }
  }

  public void showWaitList() {
    String productID = getToken("Enter product id");
    List<WaitListItem> result = warehouse.viewProductWaitList(productID);
    if (result == null) {
      System.out.println("No such product");
      return;
    }
    if (result.isEmpty()) {
      System.out.println("Waitlist is empty");
    }
    Iterator<WaitListItem> items = result.iterator();
    while (items.hasNext()) {
      WaitListItem item = items.next();
      System.out.println("Client " + item.getClient().getId() + " " + item.getClient().getName()
          + " product " + item.getProduct().getId() + " quantity " + item.getQuantity());
    }
  }

  public void recordPayment() {
    String clientID = getToken("Enter client id");
    if (warehouse.getClientByID(clientID) == null) {
      System.out.println("No such client");
      return;
    }
    double amount = getAmount("Enter payment amount");
    if (warehouse.recordPayment(clientID, amount)) {
      System.out.println("Payment recorded");
    } else {
      System.out.println("Payment could not be recorded");
    }
  }

  public void receiveShipment() {
    String productID = getToken("Enter product id");

    if (warehouse.getProductByID(productID) == null) {
      System.out.println("No such product");
      return;
    }

    int quantity = getQuantity("Enter shipment quantity", 1);

    try {
      if (warehouse.receiveShipment(productID, quantity)) {
        System.out.println(
            "Shipment received; waitlist items filled first "
                + "and remaining units added to stock.");
      } else {
        System.out.println("Shipment could not be received");
      }
    } catch (IllegalArgumentException exception) {
      System.out.println(exception.getMessage());
    }
  }

  public void showInvoices() {
    String clientID = getToken("Enter client id");
    Client client = warehouse.getClientByID(clientID);

    if (client == null) {
      System.out.println("No such client");
      return;
    }

    List<Transaction> result = warehouse.viewClientInvoices(clientID);

    if (result.isEmpty()) {
      System.out.println("There are no invoices for this client");
      return;
    }

    System.out.printf(
        "%nInvoices for %s - %s%nAddress: %s%n",
        client.getId(),
        client.getName(),
        client.getAddress());

    int invoiceCount = 0;
    double totalInvoiced = 0;
    Iterator<Transaction> invoices = result.iterator();

    while (invoices.hasNext()) {
      Transaction invoice = invoices.next();

      System.out.println();
      invoice.Invoice();

      invoiceCount++;
      totalInvoiced += invoice.getAmount();
    }

    System.out.printf(
        Locale.US,
        "%nInvoice count: %d%nTotal invoiced: $%.2f%n",
        invoiceCount,
        totalInvoiced);
  }

  public void showTransactions() {
    String clientID = getToken("Enter client id");
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

    System.out.printf(
        "%nTransactions for %s - %s%n",
        client.getId(),
        client.getName());

    int transactionCount = 0;

    while (transactions.hasNext()) {
      Transaction transaction = transactions.next();
      String type = transaction.isInvoice() ? "Invoice" : "Payment";

      System.out.printf(
          Locale.US,
          "%nTransaction ID: %d%nDate: %s%nType: %s%n"
              + "Description: %s%nAmount: $%+.2f%n",
          transaction.getTransactionId(),
          transaction.getDate(),
          type,
          transaction.getDescription(),
          transaction.getAmount());

      transactionCount++;
    }

    double balance = client.getBalance();
    String status = balance > 0 ? "owed"
        : balance < 0 ? "credit" : "zero";

    System.out.printf(
        Locale.US,
        "%nTransaction count: %d%nBalance: $%.2f (%s)%n",
        transactionCount,
        Math.abs(balance),
        status);
  }

  public void showOutstandingClients() {
    Iterator<Client> clients = warehouse.getClientsWithOutstandingBalance();

    if (!clients.hasNext()) {
      System.out.println("No clients have an outstanding balance");
      return;
    }

    System.out.printf(
        "%n%-6s %-20s %-25s %14s%n",
        "ID", "Name", "Address", "Amount owed");

    int clientCount = 0;
    double totalOwed = 0;

    while (clients.hasNext()) {
      Client client = clients.next();
      double balance = client.getBalance();

      System.out.printf(
          Locale.US,
          "%-6s %-20s %-25s $%13.2f%n",
          client.getId(),
          client.getName(),
          client.getAddress(),
          balance);

      clientCount++;
      totalOwed += balance;
    }

    System.out.printf(
        Locale.US,
        "%nClients with outstanding balances: %d%n"
            + "Total amount owed: $%.2f%n",
        clientCount,
        totalOwed);
  }

  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case ADD_CLIENT:
          addClient();
          break;
        case ADD_PRODUCTS:
          addProducts();
          break;
        case SHOW_CLIENTS:
          showClients();
          break;
        case SHOW_PRODUCTS:
          showProducts();
          break;
        case ADD_TO_WISHLIST:
          addToWishList();
          break;
        case SHOW_WISHLIST:
          showWishList();
          break;
        case PLACE_ORDER:
          placeOrder();
          break;
        case SHOW_WAITLIST:
          showWaitList();
          break;
        case RECORD_PAYMENT:
          recordPayment();
          break;
        case RECEIVE_SHIPMENT:
          receiveShipment();
          break;
        case SHOW_INVOICES:
          showInvoices();
          break;
        case SHOW_TRANSACTIONS:
          showTransactions();
          break;
        case SHOW_OUTSTANDING_CLIENTS:
          showOutstandingClients();
          break;
        case HELP:
          help();
          break;
      }
    }
  }

  public static void main(String[] s) {
    try {
      UserInterface.instance().process();
    } catch (NoSuchElementException eof) {
      System.out.println("End of input; exiting.");
    } catch (UncheckedIOException ioe) {
      System.err.println("Could not read input: " + ioe.getCause().getMessage());
    }
  }
}
