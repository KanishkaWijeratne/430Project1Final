import java.io.*;
import java.util.*;

public class Product implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String id;
  private final String name;
  private int amountInStock;
  private final double salePrice;
  private final List<WaitListItem> waitList = new LinkedList<>();

  public Product(String id, String name, int amountInStock, double salePrice) {
    this.id = id;
    this.name = name;
    this.amountInStock = amountInStock;
    this.salePrice = salePrice;
  }

  public String getName() {
    return name;
  }

  public int getAmountInStock() {
    return amountInStock;
  }

  public double getSalePrice() {
    return salePrice;
  }

  public void addToWaitList(Client client, int quantity) {
    addToWaitList(client, quantity, null);
  }

  public void addToWaitList(Client client, int quantity, Order order) {
    waitList.add(new WaitListItem(client, this, quantity, order));
  }

  public List<WaitListItem> getWaitList() {
    List<WaitListItem> copy = new ArrayList<>();
    Iterator<WaitListItem> iterator = waitList.iterator();

    while (iterator.hasNext()) {
      WaitListItem item = iterator.next();

      copy.add(new WaitListItem(
          item.getClient(),
          this,
          item.getQuantity(),
          item.getOrder()));
    }

    return copy;
  }

  public long getTotalWaitlistedQuantity() {
    long total = 0;
    Iterator<WaitListItem> iterator = waitList.iterator();

    while (iterator.hasNext()) {
      total += iterator.next().getQuantity();
    }

    return total;
  }

  public List<WaitListItem> receiveShipment(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive.");
    }

    long leftover = Math.max(
        0L, (long) quantity - getTotalWaitlistedQuantity());

    if ((long) amountInStock + leftover > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(
          "Shipment would exceed the supported stock quantity.");
    }

    List<WaitListItem> fulfilledItems = new ArrayList<>();
    int remaining = quantity;
    Iterator<WaitListItem> iterator = waitList.iterator();

    while (iterator.hasNext() && remaining > 0) {
      WaitListItem waiting = iterator.next();

      int supplied = Math.min(remaining, waiting.getQuantity());

      fulfilledItems.add(new WaitListItem(
          waiting.getClient(),
          this,
          supplied,
          waiting.getOrder()));

      waiting.reduceQuantity(supplied);
      remaining -= supplied;

      if (waiting.getQuantity() == 0) {
        iterator.remove();
      }
    }

    addToStock(remaining);
    return fulfilledItems;
  }

  public void removeFromStock(int quantity) {
    if (quantity < 0 || quantity > amountInStock) {
      throw new IllegalArgumentException("Quantity exceeds available stock.");
    }
    amountInStock -= quantity;
  }

  public void addToStock(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity must not be negative.");
    }
    amountInStock += quantity;
  }

  public String getId() {
    return id;
  }

  public boolean equals(String id) {
    return this.id.equals(id);
  }

  @Override
  public String toString() {
    String string = "Product name " + name + " amount in stock " + amountInStock
        + " sale price " + salePrice + " id " + id;
    return string;
  }
}