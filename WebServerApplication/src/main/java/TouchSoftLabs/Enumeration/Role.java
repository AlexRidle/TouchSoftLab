package TouchSoftLabs.Enumeration;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
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

    @Override
    public String getAuthority() {
        return name();
    }
}
