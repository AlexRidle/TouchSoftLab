package Enumeration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TestRole {

    @Test
    public void testGetUserRole() {
        boolean agentExists = Role.isRoleExists("agent");
        boolean clientExists = Role.isRoleExists("client");
        boolean roleNotExists = Role.isRoleExists("unlisted_role");
        assertTrue(agentExists);
        assertTrue(clientExists);
        assertFalse(roleNotExists);
    }
}
