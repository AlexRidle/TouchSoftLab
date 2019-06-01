package Client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

class MockDataClient {
    static HashMap<String, String> getClientInfo(){
        HashMap<String, String> clientInfo = new HashMap<>();
        clientInfo.put("role", "agent");
        clientInfo.put("name", "smith");
        return clientInfo;
    }

    static BufferedReader getSystemIn(){
        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream("/register agent smith".getBytes()));
        return new BufferedReader(inputStreamReader);
    }
}

