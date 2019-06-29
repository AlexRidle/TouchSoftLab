package TouchSoftLabs.Service;

import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Entity.Message;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.Session;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestServerService {

    private ServerService serverService;
    private Client client;

    @Before
    public void init() {
        serverService = mock(ServerService.class);
        client = mock(Client.class);
    }

//    @Test
//    public void testRegisterClient() {
//        when(serverService.registerClient(anyString(), anyString(), eq(null))).thenReturn(client);
//        assertEquals(client, serverService.registerClient(anyString(), anyString(), eq(null)));
//    }

//    @Test
//    public void testSendMessage() throws Exception {
//        Message message = mock(Message.class);
//        Session session = mock(Session.class);
//        client.setSession(session);
//        serverService.sendMessage(message, client);
//        verify(serverService).sendMessage(message, client);
//    }
}
