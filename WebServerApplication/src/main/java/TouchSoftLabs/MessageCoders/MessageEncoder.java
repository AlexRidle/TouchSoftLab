package TouchSoftLabs.MessageCoders;

import TouchSoftLabs.Entity.Message;
import com.google.gson.Gson;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    private static Gson gson = new Gson();

    @Override
    public String encode(Message message) {
        return gson.toJson(message);
    }

    @Override
    public void init(final EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}