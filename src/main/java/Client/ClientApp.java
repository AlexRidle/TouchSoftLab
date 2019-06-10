package Client;

import Service.ApplicationProperties;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApp {

    private final ClientService clientService;
    private final Properties properties;
    private final BufferedReader systemIn;
    private final JSONObject jsonClientInfo;
    private final ExecutorService executorService;
    private String HOST;
    private int PORT;
    private Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;

    public ClientApp() {
        executorService = Executors.newFixedThreadPool(2);
        properties = ApplicationProperties.getProperties();
        clientService = new ClientService();
        systemIn = new BufferedReader(new InputStreamReader(System.in));
        jsonClientInfo = clientService.clientRegister(systemIn);

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
    }

    public void main(){
        try {
            socket = new Socket(HOST, PORT);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (UnknownHostException e) {
            System.out.println(String.format("Произошла ошибка при подключении к серверу по адресу \"%s:%s\". " +
                    "Проверьте значения в файле конфигурации приложения", HOST, PORT));
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при подключении к серверу");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            socketOut.write(jsonClientInfo.toString() + "\n");
            socketOut.flush();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при отправке информации на сервер");
            clientService.close(socket);
            e.printStackTrace();
        }

        System.out.println("Для отправки сообщения введите текст");
        executorService.submit(new ClientReceiver(socket, socketIn, clientService));
        executorService.submit(new ClientSender(socket, socketOut, clientService));
    }
}