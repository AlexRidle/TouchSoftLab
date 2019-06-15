package Service;

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
}
