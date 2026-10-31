public class LoginState extends WarehouseState {
    public LoginState(WarehouseContext context) {
        super(context);
    }

    @Override
    public int run() {
        System.out.println("\nLogin menu");
        System.out.println("1. Login as client");
        System.out.println("2. Login as clerk");
        System.out.println("3. Login as manager");
        System.out.println("0. Exit");
        int command = ui.getNumber("Enter command");
        if (command == 0) {
            return context.exit();
        }
        if (command < 1 || command > 3) {
            System.out.println("Enter a command from 0 to 3");
            return context.stay();
        }

        if (command == 1) {
            String clientID = ui.getToken("Enter client ID");
            return context.loginAsClient(clientID);
        }

        String username = ui.getToken("Enter username");
        String password = ui.getToken("Enter password");

        if (command == 2) {
            return context.loginAsClerk(username, password);
        }
        return context.loginAsManager(username, password);
    }
}
