package Client;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestClientService {

    private JSONObject jsonClientInfo;
    private JSONObject jsonMessage;
    private Socket socket;
    private ClientService mockClientService;
    private ClientService clientService;
    private BufferedReader systemIn;
    private BufferedWriter socketOut;


    @Before
    public void init() {
        jsonClientInfo = MockDataClient.getJsonClientInfo();
        jsonMessage = MockDataClient.getJsonMessage();
        socket = mock(Socket.class);
        mockClientService = mock(ClientService.class);
        clientService = new ClientService();
        systemIn =  MockDataClient.getSystemIn();
        socketOut = mock(BufferedWriter.class);
    }

    @Test
    public void testClientRegister() {
        when(mockClientService.clientRegister(systemIn)).thenReturn(jsonClientInfo);
        assertEquals(mockClientService.clientRegister(systemIn),jsonClientInfo);
    }

    @Test
    public void testSend() throws IOException {
        clientService.send("message",socketOut);
        verify(socketOut,atLeastOnce()).write(jsonMessage.toString() + "\n");
        verify(socketOut).flush();
    }

    @Test
    public void testClose(){
        mockClientService.close(socket);
        verify(mockClientService).close(socket);
    }

}
