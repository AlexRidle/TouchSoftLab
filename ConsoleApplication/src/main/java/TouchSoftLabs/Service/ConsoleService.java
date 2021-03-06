package TouchSoftLabs.Service;

import TouchSoftLabs.Client.WebConsoleClient;
import TouchSoftLabs.Enumeration.Role;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
            HashMap<String,String> headers = new HashMap<>();
            headers.put("Authorization", "b15FeJyByMQ6JlT0/QnJRQ==");
            headers.put("Cookie", "JSESSIONID=7F58FC12681A929A39F37971CBD3A0CC");
            client = new WebConsoleClient(getClientURI(splittedInput[1], splittedInput[2]),headers);
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

        String HOST = "localhost";
        int PORT = 8080;

        try {
            HOST = ApplicationProperties.getProperties().getProperty("HOST", "localhost");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при настройке адреса сервера. " +
                    "Проверьте значение адреса в файле конфигурации приложения.");
            System.out.println("В качестве хоста используется значение по умолчанию: " + HOST);
        }

        try {
            PORT = Integer.valueOf(properties.getProperty("PORT"));
        } catch (Exception e) {
            System.out.println("Произошла ошибка при настройке порта сервера. " +
                    "Проверьте значение порта в файле конфигурации приложения");
            System.out.println("В качестве порта используется значение по умолчанию: " + PORT);
        }

        return new URI(String.format("ws://%s:%s/%s/%s/", HOST, PORT, role, name));
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
                while (true) {
                    message = systemIn.readLine().trim();
                    if (!message.equals("")) {
                        break;
                    }
                    System.out.println("Сообщение не может быть пустым!");
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", message);
                jsonObject.put("timestamp", getTimeStamp());
                client.send(jsonObject.toString());
                if (message.equalsIgnoreCase("/exit")) {
                    client.close();
                    break;
                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка при вводе сообщения");
                e.printStackTrace();
            }
        }
    }

    private String getTimeStamp() {
        return String.format("[%s]", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
    }

    public void run() {
        WebSocketClient client = clientRegister();
        System.out.println("Для отправки сообщения введите текст");
        sendMessage(client);
    }

}
