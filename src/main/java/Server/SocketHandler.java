package Server;

import Service.ExceptionUtils;
import Service.ServerLogger;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
public class SocketHandler implements Runnable{

    private final ServerSocket serverSocket;
    private final HashSet<UserSocket> userSockets;

    @Override
    public void run(){
        while(true){
            Socket socket = getNewConnection();
            if (socket != null) {
                try{
                    UserSocket userSocket = new UserSocket(socket, userSockets);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.submit(userSocket);
                    userSockets.add(userSocket);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private Socket getNewConnection() {
        Socket socket = null;
        try{
            socket = serverSocket.accept();
        } catch (IOException e){
            ServerLogger.logError(String.format("Произошла ошибка соединения с клиентом.\r\n%s", ExceptionUtils.getStackTrace(e)));
        }
        return socket;
    }
}
