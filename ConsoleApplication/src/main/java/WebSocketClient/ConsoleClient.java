package WebSocketClient;

import Service.ApplicationUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;


public class ConsoleClient extends WebSocketClient {

    public ConsoleClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Соединение установлено. Handshake: " + handshake.getHttpStatusMessage());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(String.format("Соединение закрыто. Причина: \"%s\".", reason));
    }

    @Override
    public void onError(Exception e) {
        System.err.println("Error:" + ApplicationUtils.convertThrowableToString(e));
    }

    @Override
    public void onMessage(String rawMessage) {
        JSONObject jsonMessage = new JSONObject(rawMessage);
        String sender = jsonMessage.getString("sender");
        String timestamp = jsonMessage.getString("timestamp");
        String message = jsonMessage.getString("message");
        System.out.println(String.format("%s %s: %s", timestamp,sender,message));
    }

    /////////////////////////////////////////////////
    @Override                                      //
    public void onMessage(ByteBuffer message) {    //
        System.out.println("received ByteBuffer"); //
    }                                              //
    /////////////////////////////////////////////////
}