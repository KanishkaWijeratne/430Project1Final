import java.io.*;
import java.util.*;


public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String address;
    private final List<WishListItem> wishlist = new LinkedList<>();
    private final List<Transaction> transactions = new LinkedList<>();

    public List<Transaction> getTransactions(){
        return transactions;
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
        wishlist.remove(item);
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