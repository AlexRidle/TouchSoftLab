package Client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestClientApp {

    @Test
    public void testClientApp(){
        ClientApp clientApp = mock(ClientApp.class);
        clientApp.main();
        verify(clientApp).main();
    }
}
