package Server;

import Service.ApplicationProperties;
import Service.ExceptionUtils;
import Service.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private ServerSocket serverSocket;
    private HashSet<UserSocket> userSockets;
    private Properties properties;
    private ExecutorService executorService;
    private int PORT;

    public ServerApp(){
        properties = ApplicationProperties.getProperties();
        PORT = Integer.valueOf(properties.getProperty("PORT"));
        executorService = Executors.newSingleThreadExecutor();
        try {
            serverSocket = new ServerSocket(PORT);
            userSockets = new HashSet<>();
            ServerLogger.logInfo(String.format("Сервер был запущен на порту %s", PORT)
            );
        } catch (IOException e) {
            ServerLogger.logError(String.format("Произошла ошибка при запуске сервера на порту %s.\r\n%s", PORT, ExceptionUtils.getStackTrace(e)));
            System.exit(0);
        }
        executorService.submit(new SocketHandler(serverSocket, userSockets));
    }
}
