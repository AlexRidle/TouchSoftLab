package Server;

import Service.ApplicationProperties;
import Service.ApplicationUtils;
import Service.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private final Properties properties;
    private final ExecutorService executorService;
    private final HashSet<UserSocket> userSockets;
    private int PORT;
    private ServerSocket serverSocket;

    public ServerApp(){
        executorService = Executors.newSingleThreadExecutor();
        properties = ApplicationProperties.getProperties();
        userSockets = new HashSet<>();
        try {
            PORT = Integer.valueOf(properties.getProperty("PORT"));
        } catch (IllegalArgumentException e) {
            ServerLogger.logError(String.format("Произошла ошибка при настройке порта сервера. Проверьте значение порта в файле конфигурации приложения.\r\n%s", ApplicationUtils.convertThrowableToString(e)));
            System.exit(0);
        }
        try {
            serverSocket = new ServerSocket(PORT);
            ServerLogger.logInfo(String.format("Сервер был запущен на порту %s", PORT)
            );
        } catch (IOException e) {
            ServerLogger.logError(String.format("Произошла ошибка при запуске сервера на порту %s.\r\n%s", PORT, ApplicationUtils.convertThrowableToString(e)));
            System.exit(0);
        }
    }

    public void main(){
        executorService.submit(new SocketHandler(serverSocket, userSockets));
    }
}
