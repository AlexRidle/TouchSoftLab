package Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {
    public static Properties getProperties() {

        Properties properties = new Properties();

        try (InputStream input = ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties")) {

            if (input == null) {
                System.out.println("Файл конфигурации \"application.properties\" не найден");
                System.exit(0);
            }

            properties.load(input);

            return properties;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
