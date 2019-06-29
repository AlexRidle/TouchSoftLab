package TouchSoftLabs.MessageCoders;

import TouchSoftLabs.Entity.Message;
import com.google.gson.JsonObject;

class MockDataMessageCoders {

    static JsonObject getJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from","from");
        jsonObject.addProperty("content","content");
        jsonObject.addProperty("timestamp","timestamp");
        return jsonObject;
    }

    static Message getMessage() {
        Message message = new Message();
        message.setFrom("from");
        message.setContent("content");
        message.setTimestamp("timestamp");
        return message;
    }

}
