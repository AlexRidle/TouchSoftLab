package Server;

import java.util.HashMap;

class MockDataServer {
    static HashMap<String, String> getClientInfo(){
        HashMap<String, String> clientInfo = new HashMap<>();
        clientInfo.put("role", "agent");
        clientInfo.put("name", "smith");
        return clientInfo;
    }
}

