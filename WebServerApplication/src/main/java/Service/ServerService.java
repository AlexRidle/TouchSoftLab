package Service;

import Entity.Client;
import Entity.Message;
import Enumeration.MessageType;
import Enumeration.Role;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;

import static Utils.ChatUtils.getTimeStamp;

public class ServerService {

    private final HashMap<String, Client> users;

    public ServerService(HashMap<String, Client> users) {
        this.users = users;
    }

    public Client registerClient(String name, String role, Session session) {
        Client client;
        switch (Role.getUserRole(role)) {
            case AGENT:
                client = getNewClient(name, Role.AGENT, true, session);
                break;
            default:
                client = getNewClient(name, Role.CLIENT, true, session);
                break;
        }
        users.put(session.getId(), client);
        return client;
    }

    private Client getNewClient(String name, Role role, boolean isFree, Session session) {
        final Client client = new Client();
        client.setName(name);
        client.setRole(role);
        client.setFree(isFree);
        client.setSession(session);
        return client;
    }

    private boolean connectFreeAgentToClient(HashMap<String, Client> users, Client client) throws IOException, EncodeException {
        Message message;
        Client agent = getFreeAgent(users);
        client.setConnectedSession(
                agent == null ? null : agent.getSession()
        );
        if (agent == null) {
            message = new Message("SERVER", "На данный момент все агенты заняты. Попробуйте повторить отправку сообщения позднее", getTimeStamp());
            sendMessage(message, client);
            ServerLogger.logWarn(String.format("Пользователь \"%s\" пытался установить соединение, но не нашел свободных агентов", client.getName()));
            return false;
        } else {
            agent.setConnectedSession(client.getSession());
            message = new Message("SERVER", String.format("Вы были подключены к агенту %s", agent.getName()), getTimeStamp());
            sendMessage(message, client);

            message = new Message("SERVER", String.format("К вам подключился пользователь %s", client.getName()), getTimeStamp());
            sendMessage(message, agent);
            ServerLogger.logInfo(String.format("Пользователь \"%s\" соединился с агентом \"%s\"", client.getName(), agent.getName()));

            return true;
        }
    }

    private Client getFreeAgent(final HashMap<String, Client> users) {
        for (Client client : users.values()) {
            if (client.getRole() == Role.AGENT && client.isFree()) {
                client.setFree(false);
                return client;
            }
        }
        return null;
    }

    public void handleMessage(final Session session, final Message message) throws IOException, EncodeException {
        Client client = users.get(session.getId());

        switch (MessageType.getMessageType(message.getContent())) {
            case RAW_MESSAGE:
                message.setFrom(client.getName());
                sendMessage(message, client);

                if (client.getConnectedSession() != null) {
                    sendMessage(message, users.get(client.getConnectedSession().getId()));
                } else if(client.getRole() == Role.CLIENT) {
                    synchronized (users) {
                        if (connectFreeAgentToClient(users, client))
                            sendMessage(message, users.get(client.getConnectedSession().getId()));
                    }
                } else {
                    message.setFrom("SERVER");
                    message.setContent("Собеседник отсутствует.");
                    message.setTimestamp(getTimeStamp());
                    sendMessage(message, client);
                }

                break;

            case LEAVE_MESSAGE:
                disconnectUser(client);
                break;
        }
    }

    public void closeConnection(Session session) throws IOException, EncodeException {
        Client client = users.get(session.getId());
        Message message;

        if (client.getConnectedSession() != null) {
            Client connectedClient = users.get(client.getConnectedSession().getId());
            message = new Message("SERVER", String.format("%s завершил диалог.", client.getName()), getTimeStamp());
            sendMessage(message,connectedClient);

            ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от пользователя \"%s\"",
                    client.getName(),
                    connectedClient.getName())
            );

            connectedClient.setConnectedSession(null);
            connectedClient.setFree(true);
        }

        users.remove(session.getId());
        ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от чата", client.getName()));
    }

    private void disconnectUser(Client client) throws IOException, EncodeException {
        Message message;
        if (client.getConnectedSession() != null) {
            Client connectedClient = users.get(client.getConnectedSession().getId());

            message = new Message("SERVER", "Вы вышли из диалога", getTimeStamp());
            sendMessage(message, client);

            message = new Message("SERVER", String.format("%s вышел из диалога.", client.getName()), getTimeStamp());
            sendMessage(message, connectedClient);

            ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от пользователя \"%s\"", client.getName(), connectedClient.getName()));

            connectedClient.setConnectedSession(null);
            connectedClient.setFree(true);
            client.setConnectedSession(null);
            client.setFree(true);
        } else {
            message = new Message("SERVER", "Команда не выполнена. Вы не подключены к собеседнику.", getTimeStamp());
            sendMessage(message, client);
        }
    }

    public void sendMessage(Message message, Client clientUser) throws IOException, EncodeException {
        clientUser.getSession().getBasicRemote().sendObject(message);
    }
}
