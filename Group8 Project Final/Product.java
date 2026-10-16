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
 
  //adding waitlsist item to product waitlist
  public void addToWaitList(Client client, int quantity) {
    if (client == null || quantity <= 0) {
      throw new IllegalArgumentException("Client must not be null and quantity must be positive.");
    }
    waitList.add(new WaitListItem(client, this, quantity));
  }


  public List<WaitListItem> getWaitList() {
    List<WaitListItem> copy = new ArrayList<>();
    Iterator<WaitListItem> items = waitList.iterator();

    while (items.hasNext()) {
        WaitListItem item = items.next();
        copy.add(new WaitListItem( item.getClient(), item.getProduct(), item.getQuantity()));
      }
    return copy;
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