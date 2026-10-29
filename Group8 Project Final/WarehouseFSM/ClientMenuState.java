public class ClientMenuState extends WarehouseState {
    public ClientMenuState(WarehouseContext context) {
        super(context);
    }

    @Override
    public int run() {
        String clientID = context.getClientID();
        System.out.println("\nClient menu: " + clientID);
        System.out.println("1. Show client details");
        System.out.println("2. Show products and prices");
        System.out.println("3. Show client transactions");
        System.out.println("4. Add item to wishlist");
        System.out.println("5. Display wishlist");
        System.out.println("6. Place an order");
        System.out.println("7. Logout");
        int command = ui.getNumber("Enter command");
        switch (command) {
            case 1:
                ui.showClientDetails(clientID);
                break;
            case 2:
                ui.showProducts(false);
                break;
            case 3:
                ui.showTransactions(clientID);
                break;
            case 4:
                ui.addToWishList(clientID);
                break;
            case 5:
                ui.showWishList(clientID);
                break;
            case 6:
                ui.placeOrder(clientID);
                break;
            case 7:
                return context.logout();
            default:
                System.out.println("Enter a command from 1 to 7");
        }
        return context.stay();
    }
}