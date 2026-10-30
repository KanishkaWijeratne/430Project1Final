public class Security {
  private final String MANAGER_CREDENTIALS = "manager";
  private final String CLERK_CREDENTIALS = "clerk";

  private final Warehouse warehouse;

  public Security(Warehouse warehouse) {
    this.warehouse = warehouse;
  }

  
  public boolean verifyLogin(Role role, String username, String password) {
    if (role == null || username == null || password == null) {
      return false;
    }
    switch (role) {
      case CLIENT:
        return username.equals(password) && warehouse.getClientByID(username) != null;
      case CLERK:
        return CLERK_CREDENTIALS.equals(username) && CLERK_CREDENTIALS.equals(password);
      case MANAGER:
        return MANAGER_CREDENTIALS.equals(username) && MANAGER_CREDENTIALS.equals(password);
      default:
        return false;
    }
  }

  public boolean verifyManagerPassword(String password) {
    return MANAGER_CREDENTIALS.equals(password);
  }
}

