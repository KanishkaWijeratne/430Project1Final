import java.io.*;
import java.util.*;


public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String address;
    private final List<WishListItem> wishlist = new LinkedList<>();
    private final List<Transaction> transactions = new LinkedList<>();
    private final List<Order> orders = new LinkedList<>(); // added order here

   
    //added getOrders to client
    public List<Order> getOrders(){
        List<Order> copy = new LinkedList<>();
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()){
            Order order = iterator.next();
            copy.add(order);
        }
        return copy;
    }
    
    public void addOrder(Order order){
        orders.add(order);
    }

    public List<Transaction> getInvoices(){
        List<Transaction> invoices = new LinkedList<>();
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()){
            Transaction transaction = iterator.next();
            if (transaction.isInvoice()){
                invoices.add(transaction);
            }
        }
        return invoices;
    }


    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public void removeFromWishList(WishListItem item){
        if (item == null) {
            return;
        }
        Iterator<WishListItem> iterator = wishlist.iterator();
        while (iterator.hasNext()) {
            WishListItem storedItem = iterator.next();
            if (storedItem.getProduct().getId().equals(item.getProduct().getId())) {
                iterator.remove();
                return;
            }
        }
    }

    public double getBalance(){
        double balance = 0.0;
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()){
            Transaction transaction = iterator.next();
            balance += transaction.getAmount();
        }
        return balance;
    }

    public Client(String id, String name, String address){
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getId(){
        return id;
    }


    public List<WishListItem> getWishList(){
        List<WishListItem> copy = new LinkedList<>();
        Iterator<WishListItem> iterator = wishlist.iterator();
        while (iterator.hasNext()){
            WishListItem item = iterator.next();
            copy.add(new WishListItem(item.getProduct(),item.getQuantity()));
        }
        return copy;
    }

    public WishListItem addToWishList(Product product, int quantity){
        Iterator<WishListItem> iterator = wishlist.iterator();
        while (iterator.hasNext()){
            WishListItem item = iterator.next();
            if (item.getProduct().getId().equals(product.getId())){
                item.setQuantity(quantity);
                return item;
            }
        }
        WishListItem newItem = new WishListItem(product, quantity);
        wishlist.add(newItem);
        return newItem;
    }

    public boolean equals(String id){
        return this.id.equals(id);
    }

    @Override
    public String toString(){
        String string = "Client name " + name + " address " + address + " id " + id;
        return string;
    }
}