package Service;

import java.util.HashMap;
import java.util.Properties;

class MockDataService {

    static Properties getProperties(){
        final Properties properties = new Properties();
        properties.setProperty("HOST","localhost");
        properties.setProperty("PORT","8080");
        return properties;
    }

    static HashMap<String, String> getHashMap(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name","string");
        return hashMap;
    }

    static String getString(){
        return "{name=string}";
    }
}
