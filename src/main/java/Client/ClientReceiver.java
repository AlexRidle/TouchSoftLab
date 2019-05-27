package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ClientReceiver implements Runnable {
    private ClientService clientService;
    private Socket socket;
    private BufferedReader socketIn;

    ClientReceiver(final Socket socket, final BufferedReader socketIn, final ClientService clientService) {
        this.socket = socket;
        this.socketIn = socketIn;
        this.clientService = clientService;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            String message = null;
            try {
                message = socketIn.readLine();
            } catch (IOException e) {
                System.out.println("Соединение было разорвано");
                clientService.close(socket);
            }
            if (message == null){
                System.out.println("Сервер разорвал соединение");
                clientService.close(socket);
            } else {
                System.out.println(message);
            }
        }
    }
}
