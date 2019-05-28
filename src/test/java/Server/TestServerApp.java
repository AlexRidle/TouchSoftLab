package Server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestServerApp {

    @Test
    public void testClientApp(){
        ServerApp serverApp = mock(ServerApp.class);
        serverApp.main();
        verify(serverApp).main();
    }
}
