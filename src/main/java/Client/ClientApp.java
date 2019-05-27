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

public class ClientApp {

    private final String HOST;
    private final int PORT;
    private final ClientService clientService;
    private BufferedReader systemIn;
    private HashMap<String, String> clientInfo;
    private Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;

    public ClientApp(){
        systemIn = new BufferedReader(new InputStreamReader(System.in));
        HOST = ApplicationProperties.getProperties().getProperty("HOST");
        PORT = Integer.parseInt(ApplicationProperties.getProperties().getProperty("PORT"));

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
        new Thread(new ClientReceiver(socket, socketIn, clientService)).start();
        new Thread(new ClientSender(socket, socketOut, clientService)).start();
    }

}
