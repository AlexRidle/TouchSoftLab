package TouchSoftLabs.MessageCoders;

import TouchSoftLabs.Entity.Message;
import com.google.gson.Gson;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

    private static Gson gson = new Gson();

    @Override
    public Message decode(String rawMessage){
        return gson.fromJson(rawMessage, Message.class);
    }

    @Override
    public boolean willDecode(String rawMessage) {
        return (rawMessage != null);
    }

    @Override
    public void init(final EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}