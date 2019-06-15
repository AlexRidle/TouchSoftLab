package Service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestConsoleService {

    @Test
    public void testConsoleService(){
        ConsoleService consoleService = mock(ConsoleService.class);
        consoleService.run();
        verify(consoleService).run();
    }

//    @Test
//    public void testRegisterClient(){
//        clientService.registerClient(jsonClientInfo,userSocket);
//        verify(clientService,atLeastOnce()).registerClient(jsonClientInfo,userSocket);
//    }

}
