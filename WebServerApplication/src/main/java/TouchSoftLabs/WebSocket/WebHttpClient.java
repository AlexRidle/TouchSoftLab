package TouchSoftLabs.WebSocket;

import TouchSoftLabs.Entity.Message;
import TouchSoftLabs.MessageCoders.MessageDecoder;
import TouchSoftLabs.MessageCoders.MessageEncoder;
import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;

public class WebHttpClient extends WebSocketClient {

    private static MessageDecoder messageDecoder;
    private static MessageEncoder messageEncoder;
    @Getter
    private static HashMap<String, WebHttpClient> webSocketClients = new HashMap<>();

    private final LinkedList<Message> newMessages;

    public WebHttpClient(URI serverURI) {
        super(serverURI);
        messageDecoder = new MessageDecoder();
        messageEncoder = new MessageEncoder();
        newMessages = new LinkedList<>();
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
        Message message = messageDecoder.decode(rawMessage);

        if(message.getFrom().equals("SERVER") && message.getContent().startsWith("Ваш идентификатор сессии: ")){
            webSocketClients.put(message.getContent().replace("Ваш идентификатор сессии: ", ""), this);
            return;
        }

        synchronized (newMessages){
            newMessages.add(message);
            newMessages.notifyAll();
        }
    }

    public synchronized String convertListToStringAndClearNewMessages() throws InterruptedException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        synchronized (newMessages){
            if(newMessages.isEmpty()){
                newMessages.wait(30000); //1000 is 1 second
            }

            if(!newMessages.isEmpty()){
                for (Message message: newMessages) {
                    jsonArray.put(messageEncoder.encode(message));
                }
                newMessages.clear();
            }
        }

        jsonObject.put("newMessages", jsonArray);
        return jsonObject.toString();
    }
}