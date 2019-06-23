package Server;

import Entity.Client;
import Entity.Message;
import MessageCoders.MessageDecoder;
import MessageCoders.MessageEncoder;
import Service.ServerLogger;
import Utils.ApplicationUtils;
import Service.ServerService;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import static Utils.ChatUtils.getTimeStamp;

@ServerEndpoint(value = "/{userrole}/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class WebServerEndpoint {

    private static HashMap<String, Client> users = new HashMap<>();
    // Есть вероятность, что к usersQueue могут получить доступ одновременно несколько потоков, поэтому доступ к ней желательно сделать 
    // синхронизированным или залоченным
    private static LinkedList<String> usersQueue = new LinkedList<>();
    private ServerService serverService = new ServerService(users, usersQueue);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("userrole") String userrole) throws IOException, EncodeException {
        Client client = serverService.registerClient(username, userrole, session);

        Message message = new Message();
        message.setFrom("SERVER");
        message.setTimestamp(getTimeStamp());
        message.setContent(String.format("Вы подключились к чату под логином \"%s\"", username));

        serverService.sendMessage(message, client);
        serverService.checkStoredMessagesAndConnectIfAgent(client);

        ServerLogger.logInfo(String.format("Пользователь \"%s\" подключился к чату", username));
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        serverService.handleMessage(session, message);
    }

    @OnClose
    synchronized public void onClose(Session session) throws IOException, EncodeException {
        serverService.closeConnection(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if(!throwable.getMessage().equals("Программа на вашем хост-компьютере разорвала установленное подключение")){
            ServerLogger.logError(String.format("Произошла ошибка.\r\n %s",
                    ApplicationUtils.convertThrowableToString(throwable)
            ));
        }
    }
}
