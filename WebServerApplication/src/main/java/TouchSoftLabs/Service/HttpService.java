package TouchSoftLabs.Service;

import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Entity.Message;
import org.springframework.stereotype.Service;

import javax.websocket.EncodeException;
import java.io.IOException;

import static TouchSoftLabs.Utils.ChatUtils.getTimeStamp;

@Service
public class HttpService {

    public void sendMessage(Client client, String content)  throws IOException, EncodeException {
        Message message = new Message(
                client.getName(),
                content,
                getTimeStamp()
        );
        ServerService.handleMessage(client.getSession(), message);
    }

    public boolean disconnect(final Client client) throws IOException, EncodeException {
        return ServerService.disconnectUser(client);
    }
}
