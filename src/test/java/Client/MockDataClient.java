package Client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

class MockDataClient {
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

    static BufferedReader getSystemIn(){
        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream("/register agent smith".getBytes()));
        return new BufferedReader(inputStreamReader);
    }
}

