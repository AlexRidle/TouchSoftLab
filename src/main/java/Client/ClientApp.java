package Client;

import Service.ApplicationProperties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApp {

    private final String HOST;
    private final int PORT;
    private final ClientService clientService;
    private BufferedReader systemIn;
    private HashMap<String, String> clientInfo;
    private Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;
    private ExecutorService executorService;

    public ClientApp(){
        HOST = ApplicationProperties.getProperties().getProperty("HOST");
        PORT = Integer.parseInt(ApplicationProperties.getProperties().getProperty("PORT"));
        systemIn = new BufferedReader(new InputStreamReader(System.in));
        executorService = Executors.newFixedThreadPool(2);

        clientService = new ClientService();
        clientInfo = clientService.clientRegister(systemIn);

        try {
            socket = new Socket(HOST, PORT);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Произошла ошибка при подключении к серверу");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            socketOut.write(clientInfo.toString() + "\n");
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
