package WebSocketClient;

import Service.ApplicationProperties;
import Service.Role;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class ConsoleService {

    private BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

    private WebSocketClient clientRegister() {
        WebSocketClient client = null;
        String input;
        String[] splittedInput;
        try {
            System.out.println("Введите команду /register и свою роль с именем через пробел: ");
            input = systemIn.readLine();
            splittedInput = input.split(" ");
            while (!isInputCorrect(splittedInput)) {
                System.out.println("Произошла ошибка при вводе команды. Попробуйте еще раз: ");
                input = systemIn.readLine();
                splittedInput = input.split(" ");
            }
            client = new ConsoleClient(getClientURI(splittedInput[1], splittedInput[2]));
            client.connect();
        } catch (URISyntaxException e) {
            System.out.println("Произошла ошибка при подключении к сокету");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при вводе команды");
            e.printStackTrace();
            System.exit(1);
        }
        return client;
    }

    private URI getClientURI(String role, String name) throws URISyntaxException {
        Properties properties = ApplicationProperties.getProperties();

        //default settings
        String HOST = "localhost";
        int PORT = 8080;

        try {
            HOST = ApplicationProperties.getProperties().getProperty("HOST");
        } catch (RuntimeException e) {
            System.out.println("Произошла ошибка при настройке адреса сервера. " +
                    "Проверьте значение адреса в файле конфигурации приложения");
            System.exit(0);
        }

        try {
            PORT = Integer.valueOf(properties.getProperty("PORT"));
        } catch (IllegalArgumentException e) {
            System.out.println("Произошла ошибка при настройке порта сервера. " +
                    "Проверьте значение порта в файле конфигурации приложения");
            System.exit(0);
        }

        return new URI(String.format("ws://%s:%s/%s/chat/%s/",HOST, PORT, role, name));
    }

    private boolean isInputCorrect(String[] splittedInput) {
        if (splittedInput.length == 3) {
            boolean rightCommand = splittedInput[0].equals("/register");
            boolean rightRole = Role.isRoleExists(splittedInput[1]);
            return rightCommand && rightRole;
        } else {
            return false;
        }
    }

    private void sendMessage(WebSocketClient client) {
        String message;
        while (true) {
            try {
                message = systemIn.readLine();
                if (message.equalsIgnoreCase("/exit")) {
                    client.close();
                    break;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", message);
                client.send(jsonObject.toString());
            } catch (IOException e) {
                System.out.println("Произошла ошибка при вводе сообщения");
                e.printStackTrace();
            }
        }
    }

    public void run(){
        WebSocketClient client = clientRegister();
        System.out.println("Для отправки сообщения введите текст");
        sendMessage(client);
    }

}
