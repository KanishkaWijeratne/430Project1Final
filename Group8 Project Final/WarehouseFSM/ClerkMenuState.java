public class ClerkMenuState extends WarehouseState {
    public ClerkMenuState(WarehouseContext context) {
        super(context);
    }

    @Override
    public int run() {
        System.out.println("\nClerk menu");
        System.out.println("1. Add a client");
        System.out.println("2. Show products, quantity, and price");
        System.out.println("3. Show clients");
        System.out.println("4. Show clients with outstanding balance");
        System.out.println("5. Record a client payment");
        System.out.println("6. Become a client");
        System.out.println("7. Logout");
        int command = ui.getNumber("Enter command");
        switch (command) {
            case 1:
                ui.addClient();
                break;
            case 2:
                ui.showProducts(true);
                break;
            case 3:
                ui.showClients();
                break;
            case 4:
                ui.showOutstandingClients();
                break;
            case 5:
                ui.recordPayment();
                break;
            case 6:
                return context.becomeClient(ui.getToken("Enter client id"));
            case 7:
                return context.logout();
            default:
                System.out.println("Enter a command from 1 to 7");
        }
        return context.stay();
    }
}