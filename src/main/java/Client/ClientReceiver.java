package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ClientReceiver implements Runnable {
    private final ClientService clientService;
    private final Socket socket;
    private final BufferedReader socketIn;

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
