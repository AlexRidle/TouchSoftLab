package TouchSoftLabs.Enumeration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestRole {

    @Test
    public void testGetUserRole() {
        assertEquals(Role.getUserRole("agent"),Role.AGENT);
        assertEquals(Role.getUserRole("client"),Role.CLIENT);
        assertEquals(Role.getUserRole("unlisted_role"),Role.CLIENT);
    }
}
