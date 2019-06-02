package Server;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestClientService {

    private JSONObject jsonClientInfo;
    private HashSet<UserSocket> userSockets;
    private ClientService clientService;
    private UserSocket userSocket;


    @Before
    public void init() {
        jsonClientInfo = MockDataServer.getJsonClientInfo();
        clientService = mock(ClientService.class);
        userSocket = mock(UserSocket.class);
        userSockets = mock(HashSet.class);
    }

    @Test
    public void testRegisterClient(){
        clientService.registerClient(jsonClientInfo,userSocket);
        verify(clientService,atLeastOnce()).registerClient(jsonClientInfo,userSocket);
    }

    @Test
    public void testGetFreeAgent() {
        when(clientService.getFreeAgent(userSockets)).thenReturn(userSocket);
    }

    @Test
    public void testLeave() {
        clientService.leave(userSocket);
        verify(clientService).leave(userSocket);
    }

    @Test
    public void testTryFindFreeAgent() {
        clientService.tryFindFreeAgent(userSockets,userSocket,"message");
        verify(clientService).tryFindFreeAgent(userSockets,userSocket,"message");

    }
}