package Enumeration;

public enum Role {
    CLIENT, AGENT;

    public static Role getUserRole(String role) {
        role = role.toLowerCase();
        switch (role) {
            case "agent":
                return Role.AGENT;
            default:
                return Role.CLIENT;
        }
    }
}
