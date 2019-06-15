package Server;

import org.json.JSONObject;

class MockDataServer {
    static JSONObject getJsonClientInfo(){
        JSONObject jsonClientInfo = new JSONObject();
        jsonClientInfo.put("role", "agent");
        jsonClientInfo.put("name", "smith");
        return jsonClientInfo;
    }

    static JSONObject getJsonMessage(){
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", "message");
        return jsonMessage;
    }
}

