package Client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ClientReceiver implements Runnable {
    private final ClientService clientService;
    private final Socket socket;
    private final BufferedReader socketIn;
    private JSONObject jsonMessage;

    ClientReceiver(final Socket socket, final BufferedReader socketIn, final ClientService clientService) {
        this.socket = socket;
        this.socketIn = socketIn;
        this.clientService = clientService;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            String rawMessage = null;
            try {
                rawMessage = socketIn.readLine();
            } catch (IOException e) {
                System.out.println("Соединение было разорвано");
                clientService.close(socket);
            }
            if (rawMessage == null){
                System.out.println("Сервер разорвал соединение");
                clientService.close(socket);
            } else {
                jsonMessage = new JSONObject(rawMessage);
                System.out.println(jsonMessage.getString("message"));
            }
        }
    }
}
