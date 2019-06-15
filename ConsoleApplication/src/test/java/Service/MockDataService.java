package Service;

import java.util.Properties;

class MockDataService {

    static Properties getProperties(){
        final Properties properties = new Properties();
        properties.setProperty("HOST","localhost");
        properties.setProperty("PORT","8080");
        return properties;
    }

}
