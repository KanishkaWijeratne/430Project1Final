public abstract class WarehouseState {
    protected final WarehouseContext context;
    protected final UserInterface ui;

    protected WarehouseState(WarehouseContext context) {
        this.context = context;
        this.ui = context.getUserInterface();
    }

    public abstract int run();
}