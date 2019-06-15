package Enumeration;

public enum Role {
    CLIENT, AGENT;

    public static boolean isRoleExists(final String inputRole){
        for(Role role : Role.values()){
            if (role.name().equalsIgnoreCase(inputRole)){
                return true;
            }
        }
        return false;
    }

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
