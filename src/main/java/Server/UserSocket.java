package Server;

import Service.ExceptionUtils;
import Service.Role;
import Service.ServerLogger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
public class UserSocket implements Runnable {

    private Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;
    private Client client;
    private HashSet<UserSocket> userSockets;
    private ClientService clientService;

    UserSocket(final Socket socket, final HashSet<UserSocket> userSockets) throws IOException {
        this.socket = socket;
        this.userSockets = userSockets;
        clientService = new ClientService();
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void run() {
        String message;
        while (!socket.isClosed()) {
            try {
                message = socketIn.readLine();
            } catch (IOException e) {
                close(userSockets, socket, this);
                ServerLogger.logError(String.format("Пользователь \"%s\" потерял соединение с сервером.\r\n%s",
                        this.getClient().getName(),
                        ExceptionUtils.getStackTrace(e))
                );
                break;
            }
            if (client == null) {
                clientService.registerClient(message, this);
            } else {
                if (message.equalsIgnoreCase("/leave")) {
                    clientService.leave(this);
                } else if (message.equalsIgnoreCase("/exit")) {
                    close(userSockets, socket, this);
                } else {
                    this.send(getTimeAndUser() + ": " + message, socketOut);
                    if (getClient().getConnectedUserSocket() != null) {
                        getClient().getConnectedUserSocket().send(
                                String.format( "%s: %s", getTimeAndUser(), message),
                                getClient().getConnectedUserSocket().getSocketOut()
                        );
                    } else if (client.getRole() == Role.CLIENT) {
                        synchronized (userSockets) {
                            clientService.tryFindFreeAgent(
                                    userSockets,
                                    this,
                                    String.format( "%s: %s", getTimeAndUser(), message)
                            );
                        }
                    } else {
                        this.send("Собеседник отсутствует", getSocketOut());
                    }
                }
            }
        }
    }

    private String getTimeAndUser() {
        return String.format("[%s] %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                client.getName());
    }

    void send(final String message, final BufferedWriter socketOut) {
        try {
            socketOut.write(message + "\n");
            socketOut.flush();
        } catch (IOException e) {
            close(userSockets, socket, this);
        }
    }

    private synchronized void close(final HashSet<UserSocket> userSockets, final Socket socket, final UserSocket userSocket) {
        userSockets.remove(userSocket);
        UserSocket clientUserSocket = userSocket.getClient().getConnectedUserSocket();
        if (clientUserSocket != null) {
            clientUserSocket.send(
                    String.format("%s завершил диалог.", userSocket.getClient().getName()),
                    userSocket.getClient().getConnectedUserSocket().getSocketOut()
            );
            ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от пользователя \"%s\"",
                    userSocket.getClient().getName(),
                    userSocket.getClient().getConnectedUserSocket().getClient().getName())
            );
            clientUserSocket.getClient().setConnectedUserSocket(null);
            clientUserSocket.getClient().setFree(true);
        }
        ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от чата", userSocket.getClient().getName()));
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
