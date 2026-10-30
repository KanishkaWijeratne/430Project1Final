import java.util.*;
import java.io.*;

public class ProductList implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Product> products = new LinkedList<Product>();
  private static ProductList productList;
  private static final String PRODUCT_STRING = "P";
  private static int idNum = 1;

  private ProductList() {
  }

  public static ProductList instance() {
    if (productList == null) {
      return (productList = new ProductList());
    } else {
      return productList;
    }
  }

  public Product insertProduct(String name, int amountInStock, double salePrice) {
    String id = PRODUCT_STRING + idNum;
    idNum++;
    Product product = new Product(id, name, amountInStock, salePrice);
    products.add(product);
    return product;
  }

  public Product search(String productID) {
    for (Product product : products) {
      if (product.getId().equals(productID)) {
        return product;
      }
    }
    return null;
  }

  public Iterator<Product> getProducts() {
    return products.iterator();
  }

  private void writeObject(java.io.ObjectOutputStream output) {
    try {
      output.defaultWriteObject();
      output.writeObject(productList);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private void readObject(java.io.ObjectInputStream input) {
    try {
      if (productList != null) {
        return;
      } else {
        input.defaultReadObject();
        if (productList == null) {
          productList = (ProductList) input.readObject();
        } else {
          input.readObject();
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }

  public String toString() {
    return products.toString();
  }
}