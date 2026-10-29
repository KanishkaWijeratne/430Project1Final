public class WarehouseContext {
    private static final int LOGIN = 0;
    private static final int CLIENT = 1;
    private static final int CLERK = 2;
    private static final int MANAGER = 3;
    private static final int STAY = 0;
    private static final int GO_CLIENT = 1;
    private static final int GO_CLERK = 2;
    private static final int GO_MANAGER = 3;
    private static final int LOGOUT = 4;
    private static final int EXIT = 5;
    private static final int RETURN_TO_ORIGIN = -2;
    private static final int TERMINATED = -1;

    private static WarehouseContext context;
    private final Warehouse warehouse = new Warehouse();
    private final UserInterface userInterface = new UserInterface(warehouse);
    private final WarehouseState[] states;
    private final int[][] nextState;
    private WarehouseState currentState;
    private int currentStateIndex = LOGIN;
    private int clientReturnState = LOGIN;
    private int clerkReturnState = LOGIN;
    private String clientID;
    private Client activeClient;
    private boolean running = true;

    private WarehouseContext() {
        states = new WarehouseState[] {
                new LoginState(this),
                new ClientMenuState(this),
                new ClerkMenuState(this),
                new ManagerMenuState(this)
        };
        nextState = new int[][] {
                {LOGIN, CLIENT, CLERK, MANAGER, TERMINATED, TERMINATED},
                {CLIENT, TERMINATED, TERMINATED, TERMINATED, RETURN_TO_ORIGIN, TERMINATED},
                {CLERK, CLIENT, TERMINATED, TERMINATED, RETURN_TO_ORIGIN, TERMINATED},
                {MANAGER, TERMINATED, CLERK, TERMINATED, LOGIN, TERMINATED}
        };
        currentState = states[currentStateIndex];
    }

    public static WarehouseContext instance() {
        if (context == null) {
            context = new WarehouseContext();
        }
        return context;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public String getClientID() {
        return clientID;
    }

    public Client getActiveClient() {
        return activeClient;
    }

    public void process() {
        while (running) {
            applyTransition(currentState.run());
        }
        System.out.println("Goodbye");
    }

    private void applyTransition(int transition) {
        if (transition < STAY || transition > EXIT) {
            System.out.println("Invalid transition");
            return;
        }
        int destination = nextState[currentStateIndex][transition];
        if (destination == RETURN_TO_ORIGIN) {
            destination = currentStateIndex == CLIENT ? clientReturnState : clerkReturnState;
            if (currentStateIndex == CLIENT) {
                clientID = null;
                activeClient = null;
            }
        }
        if (destination == TERMINATED) {
            if (transition != EXIT && transition != LOGOUT) {
                System.out.println("That transition is not valid from this menu");
                return;
            }
            if (currentStateIndex == CLIENT) {
                clientID = null;
                activeClient = null;
            }
            running = false;
            currentStateIndex = TERMINATED;
            currentState = null;
            return;
        }
        if (transition == GO_CLIENT) {
            if (clientID == null || warehouse.getClientByID(clientID) == null) {
                System.out.println("No such client");
                clientID = null;
                return;
            }
            clientReturnState = currentStateIndex;
            activeClient = warehouse.getClientByID(clientID);
        } else if (transition == GO_CLERK) {
            clerkReturnState = currentStateIndex;
        }
        if (transition == LOGOUT && destination == LOGIN) {
            clientID = null;
            activeClient = null;
        }
        currentStateIndex = destination;
        currentState = states[currentStateIndex];
    }

    public int loginAsClient(String id) {
        if (id == null || warehouse.getClientByID(id) == null) {
            System.out.println("No such client");
            return STAY;
        }
        clientID = id;
        return GO_CLIENT;
    }

    public int becomeClient(String id) {
        if (id == null || warehouse.getClientByID(id) == null) {
            System.out.println("No such client");
            return STAY;
        }
        clientID = id;
        return GO_CLIENT;
    }

    public int becomeClerk() {
        return GO_CLERK;
    }

    public int becomeManager() {
        return GO_MANAGER;
    }

    public int logout() {
        return LOGOUT;
    }

    public int exit() {
        return EXIT;
    }

    public int stay() {
        return STAY;
    }
}