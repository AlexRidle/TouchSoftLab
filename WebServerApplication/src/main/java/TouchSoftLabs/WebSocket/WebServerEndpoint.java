package TouchSoftLabs.WebSocket;

import TouchSoftLabs.Entity.ChatRoom;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Entity.Message;
import TouchSoftLabs.MessageCoders.MessageDecoder;
import TouchSoftLabs.MessageCoders.MessageEncoder;
import TouchSoftLabs.Service.ServerLogger;
import TouchSoftLabs.Service.ServerService;
import TouchSoftLabs.Utils.ApplicationUtils;
import lombok.Getter;

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
import java.util.HashSet;
import java.util.LinkedList;

@ServerEndpoint(value = "/{userrole}/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class WebServerEndpoint {

    @Getter
    private static HashMap<String, Client> users = new HashMap<>();
    @Getter
    private static LinkedList<String> usersQueue = new LinkedList<>();
    @Getter
    private static HashMap<Integer, ChatRoom> chatRooms = new HashMap<>();
    @Getter
    private static HashSet<String> userNames = new HashSet<>();

    private String username;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("userrole") String userrole) throws IOException, EncodeException {
        if(userNames.contains(username)){
            Client client = new Client();
            client.setSession(session);

            ServerService.sendMessageFromServerToUser("Вы уже подключены к чату. Отключитесь от него, чтобы создать новую сессию.", client);
            session.close();
        } else {
            this.username = username;
            userNames.add(username);
            Client client = ServerService.registerClient(username, userrole, session);
            ServerService.sendMessageFromServerToUser("Ваш идентификатор сессии: " + session.getId(), client);
            ServerService.sendMessageFromServerToUser(String.format("Вы подключились к чату под логином \"%s\"", username), client);
            ServerLogger.logInfo(String.format("Пользователь \"%s\" подключился к чату", username));
            ServerService.checkStoredMessagesAndConnectIfAgent(client);
        }
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        ServerService.handleMessage(session, message);
    }

    @OnClose
    synchronized public void onClose(Session session) throws IOException, EncodeException {
        userNames.remove(username);
        ServerService.closeConnection(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (!throwable.getMessage().equals("Программа на вашем хост-компьютере разорвала установленное подключение")) {
            ServerLogger.logError(String.format("Произошла ошибка.\r\n %s",
                    ApplicationUtils.convertThrowableToString(throwable)
            ));
        }
    }
}