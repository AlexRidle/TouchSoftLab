package TouchSoftLabs.WebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


public class WebHttpClient extends WebSocketClient {

    public WebHttpClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onMessage(String rawMessage) {
        //приходит сообщение http челу
        //оно должно записаться в неотправленные
        //должен вызываться метод, который их отправляет
        //отправляются либо сразу, либо с периодом в 30 секунд
    }
}