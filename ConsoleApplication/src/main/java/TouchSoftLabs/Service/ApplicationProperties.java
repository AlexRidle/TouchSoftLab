package TouchSoftLabs.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class ApplicationProperties {
    static Properties getProperties() {

        final Properties properties = new Properties();

        try (InputStream input = ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties")) {

            if (input == null) {
                System.out.println("Файл конфигурации \"application.properties\" не найден");
                System.exit(0);
            }

            properties.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
