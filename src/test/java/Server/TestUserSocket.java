package Server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestUserSocket {

    private HashSet<UserSocket> userSockets;
    private UserSocket userSocket;
    private BufferedWriter socketOut;
    private Socket socket;

    @Before
    public void init(){
        userSockets = new HashSet<>();
        userSocket = new UserSocket();
        userSockets.add(userSocket);
        userSocket.setClient(mock(Client.class));
        socketOut = mock(BufferedWriter.class);
        socket = mock(Socket.class);
    }

    @Test
    public void testSend() throws IOException {
        userSocket.send("message", socketOut);
        verify(socketOut, atLeastOnce()).write("message" + "\n");
        verify(socketOut, atLeastOnce()).flush();
    }

    @Test
    public void testClose() throws Exception {
        userSocket.close(userSockets, socket, userSocket);
        verify(socket, atLeastOnce()).close();
    }
}
