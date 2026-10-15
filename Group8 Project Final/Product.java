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

  public List<WaitListItem> getWaitList() {
    return waitList;
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