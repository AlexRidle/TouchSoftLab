package TouchSoftLabs.Server;

import TouchSoftLabs.Entity.ChatRoom;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Entity.Message;
import TouchSoftLabs.MessageCoders.MessageDecoder;
import TouchSoftLabs.MessageCoders.MessageEncoder;
import TouchSoftLabs.Service.ServerLogger;
import TouchSoftLabs.Utils.ApplicationUtils;
import TouchSoftLabs.Service.ServerService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static TouchSoftLabs.Utils.ChatUtils.getTimeStamp;

@ServerEndpoint(value = "/{userrole}/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class WebServerEndpoint {

    @Getter
    private static HashMap<String, Client> users = new HashMap<>();
    @Getter
    private static LinkedList<String> usersQueue = new LinkedList<>();
    @Getter
    private static HashMap<Integer, ChatRoom> chatRooms = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("userrole") String userrole) throws IOException, EncodeException {
        Client client = ServerService.registerClient(username, userrole, session);

        Message message = new Message();
        message.setFrom("SERVER");
        message.setTimestamp(getTimeStamp());
        message.setContent(String.format("Вы подключились к чату под логином \"%s\"", username));

        ServerService.sendMessage(message, client);
        if (client.getChatRoom() != null) {
            ServerService.closeChatRoom(client.getChatRoom());
        }

        ServerService.checkStoredMessagesAndConnectIfAgent(client);

        ServerLogger.logInfo(String.format("Пользователь \"%s\" подключился к чату", username));
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        ServerService.handleMessage(session, message);
    }

    @OnClose
    synchronized public void onClose(Session session) throws IOException, EncodeException {
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