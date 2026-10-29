public class ManagerMenuState extends WarehouseState {
    public ManagerMenuState(WarehouseContext context) {
        super(context);
    }

    @Override
    public int run() {
        System.out.println("\nManager menu");
        System.out.println("1. Add a product");
        System.out.println("2. Display a product waitlist");
        System.out.println("3. Receive a shipment");
        System.out.println("4. Become a clerk");
        System.out.println("5. Logout");
        int command = ui.getNumber("Enter command");
        switch (command) {
            case 1:
                ui.addProducts();
                break;
            case 2:
                ui.showWaitList();
                break;
            case 3:
                ui.receiveShipment();
                break;
            case 4:
                return context.becomeClerk();
            case 5:
                return context.logout();
            default:
                System.out.println("Enter a command from 1 to 5");
        }
        return context.stay();
    }
}