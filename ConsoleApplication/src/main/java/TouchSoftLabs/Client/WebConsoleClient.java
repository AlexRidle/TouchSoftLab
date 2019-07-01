package TouchSoftLabs.Client;

import TouchSoftLabs.Utils.ApplicationUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class WebConsoleClient extends WebSocketClient {

    public WebConsoleClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Открывается соединение с сервером...");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (reason.equalsIgnoreCase("")) {
            reason = "Соединение разорвано по запросу пользователя";
        }
        System.out.println(String.format("Соединение закрыто. Причина: \"%s\".", reason));
        System.exit(0);
    }

    @Override
    public void onError(Exception e) {
        System.err.println("Произошла ошибка: " + ApplicationUtils.convertThrowableToString(e));
    }

    @Override
    public void onMessage(String rawMessage) {
        JSONObject jsonMessage = new JSONObject(rawMessage);
        String message = jsonMessage.getString("content");
        String sender = jsonMessage.getString("from");
        String timestamp = jsonMessage.getString("timestamp");

        if(sender.equals("SERVER") && message.startsWith("Ваш идентификатор сессии: ")){
            return;
        }

        System.out.println(String.format("%s %s: %s", timestamp, sender, message));
    }
}