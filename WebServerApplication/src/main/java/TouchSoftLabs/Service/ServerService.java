package TouchSoftLabs.Service;

import TouchSoftLabs.Entity.ChatRoom;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Entity.Message;
import TouchSoftLabs.Enumeration.MessageType;
import TouchSoftLabs.Enumeration.Role;
import TouchSoftLabs.WebSocket.WebServerEndpoint;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import static TouchSoftLabs.Utils.ChatUtils.getTimeStamp;

public class ServerService {

    private static final HashMap<String, Client> users = WebServerEndpoint.getUsers();
    private static final LinkedList<String> usersQueue = WebServerEndpoint.getUsersQueue();
    private static final HashMap<Integer, ChatRoom> chatRooms = WebServerEndpoint.getChatRooms();

    private static int chatRoomIdCounter = 0;

    static public Client registerClient(String name, String role, Session session) {
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

    static private Client getNewClient(String name, Role role, boolean isFree, Session session) {
        final Client client = new Client();
        client.setName(name);
        client.setRole(role);
        client.setFree(isFree);
        client.setSession(session);
        client.setQueuedMessages(new LinkedList<>());
        client.setChatRoom(null);
        return client;
    }

    static private boolean connectFreeAgentToClient(Client client, Message message) throws IOException, EncodeException {
        Client agent = getFreeAgent();
        client.setConnectedSession(
                agent == null ? null : agent.getSession()
        );
        if (agent == null) {
            storeMessage(client, message);
            return false;
        } else {
            agent.setConnectedSession(client.getSession());
            client.setFree(false);
            message = new Message("SERVER", String.format("Вы были подключены к агенту %s", agent.getName()), getTimeStamp());
            sendMessage(message, client);

            message = new Message("SERVER", String.format("К вам подключился пользователь %s", client.getName()), getTimeStamp());
            sendMessage(message, agent);
            ServerLogger.logInfo(String.format("Пользователь \"%s\" соединился с агентом \"%s\"", client.getName(), agent.getName()));

            createChatRoom(client, agent);
            return true;
        }
    }

    static private synchronized void storeMessage(final Client client, final Message message) throws IOException, EncodeException {
        if (!usersQueue.contains(client.getSession().getId())) {
            sendMessage(new Message("SERVER",
                    "На данный момент все агенты заняты. На ваше сообщение ответит первый освободившийся агент",
                    getTimeStamp()), client);
            usersQueue.add(client.getSession().getId());
        }
        sendMessage(new Message("SERVER",
                "Сообщение записано. Ваша позиция в очереди: " + (usersQueue.indexOf(client.getSession().getId()) + 1),
                getTimeStamp()), client);
        client.getQueuedMessages().add(message);
    }

    static private synchronized Client getFreeAgent() {
        for (Client client : users.values()) {
            if (client.getRole() == Role.AGENT && client.isFree()) {
                client.setFree(false);
                return client;
            }
        }
        return null;
    }

    static public void handleMessage(final Session session, final Message message) throws IOException, EncodeException {
        Client client = users.get(session.getId());

        switch (MessageType.getMessageType(message.getContent())) {
            case RAW_MESSAGE:
                message.setFrom(client.getName());
                sendMessage(message, client);

                if (client.getChatRoom() != null) {
                    client.getChatRoom().getMessageList().add(message);
                }

                if (client.getConnectedSession() != null) {
                    sendMessage(message, users.get(client.getConnectedSession().getId()));
                } else if (client.getRole() == Role.CLIENT) {
                    synchronized (users) {
                        if (connectFreeAgentToClient(client, message))
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

    static public synchronized void closeConnection(Session session) throws IOException, EncodeException {
        Client client = users.get(session.getId());
        Message message;

        if (client.getChatRoom() != null) {
            closeChatRoom(client.getChatRoom());
        }

        if (client.getConnectedSession() != null) {
            Client connectedClient = users.get(client.getConnectedSession().getId());
            message = new Message("SERVER", String.format("%s завершил диалог.", client.getName()), getTimeStamp());
            sendMessage(message, connectedClient);

            ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от пользователя \"%s\"",
                    client.getName(),
                    connectedClient.getName())
            );

            connectedClient.setConnectedSession(null);
            connectedClient.setFree(true);
            checkStoredMessagesAndConnectIfAgent(connectedClient);
        } else {
            int fromIndex = usersQueue.indexOf(session.getId());
            usersQueue.remove(session.getId());
            notifyQueuedUsersFromIndex(fromIndex);
        }

        users.remove(session.getId());

        ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от чата", client.getName()));
    }

    static public synchronized boolean disconnectUser(Client client) throws IOException, EncodeException {
        Message message;

        if (client.getChatRoom() != null) {
            closeChatRoom(client.getChatRoom());
        }

        if (client.getConnectedSession() != null) {
            Client connectedClient = users.get(client.getConnectedSession().getId());

            message = new Message("SERVER", "Вы вышли из диалога", getTimeStamp());
            sendMessage(message, client);

            message = new Message("SERVER", String.format("%s вышел из диалога.", client.getName()), getTimeStamp());
            sendMessage(message, connectedClient);

            ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от пользователя \"%s\"", client.getName(), connectedClient.getName()));

            connectedClient.setConnectedSession(null);
            if (client.getRole() == Role.AGENT) {
                connectedClient.setFree(true);
                client.setConnectedSession(null);
                if (!usersQueue.isEmpty()) {
                    checkStoredMessagesAndConnectIfAgent(client);
                } else {
                    client.setFree(true);
                }
            } else {
                if (!usersQueue.isEmpty()) {
                    checkStoredMessagesAndConnectIfAgent(connectedClient);
                } else {
                    connectedClient.setFree(true);
                }
                client.setConnectedSession(null);
                client.setFree(true);
            }
            return true;
        } else if (usersQueue.contains(client.getSession().getId())) {
            int fromIndex = usersQueue.indexOf(client.getSession().getId());
            usersQueue.remove(client.getSession().getId());
            client.getQueuedMessages().clear();
            notifyQueuedUsersFromIndex(fromIndex);
            sendMessage(new Message("SERVER", "Вы покинули очередь. История ваших сообщений очищена.", getTimeStamp()), client);
            return true;
        } else {
            sendMessage(new Message("SERVER", "Команда не выполнена. Вы не подключены к собеседнику.", getTimeStamp()), client);
            return false;
        }
    }

    static public void sendMessage(Message message, Client clientUser) throws IOException, EncodeException {
        clientUser.getSession().getBasicRemote().sendObject(message);
    }

    static public synchronized void checkStoredMessagesAndConnectIfAgent(final Client agent) throws IOException, EncodeException {
        if (agent.getRole() == Role.AGENT) {
            if (!usersQueue.isEmpty()) {
                Client client = users.get(usersQueue.getFirst());
                usersQueue.removeFirst();
                agent.setConnectedSession(client.getSession());
                client.setConnectedSession(agent.getSession());
                agent.setFree(false);
                client.setFree(false);

                createChatRoom(client, agent);

                sendMessage(new Message("SERVER",
                        String.format("К вам подключился пользователь %s. Пропущенные сообщения:", client.getName()),
                        getTimeStamp()), agent);
                for (Message message : client.getQueuedMessages()) {
                    sendMessage(message, agent);
                    client.getChatRoom().getMessageList().add(message);
                }
                client.getQueuedMessages().clear();

                if (usersQueue.size() > 0) {
                    sendMessage(new Message("SERVER",
                            String.format("Количество пользователей ожидающих ответа: %s", usersQueue.size()),
                            getTimeStamp()), agent
                    );
                }
                sendMessage(new Message("SERVER",
                        String.format("Вы были подключены к агенту %s. Ваши ранее написанные сообщения были отправлены.", agent.getName()),
                        getTimeStamp()), client
                );

                notifyQueuedUsersFromIndex(0);
                ServerLogger.logInfo(String.format("Пользователь \"%s\" соединился с агентом \"%s\"", client.getName(), agent.getName()));
            }


        }

    }

    static private synchronized void notifyQueuedUsersFromIndex(int fromIndex) throws IOException, EncodeException {
        Client client;
        for (int index = fromIndex; index < usersQueue.size() && index != -1; index++) {
            client = users.get(usersQueue.get(index));
            sendMessage(new Message("SERVER",
                    "Ваша позиция в очереди: " + (index + 1),
                    getTimeStamp()), client);
        }
    }

    static private synchronized void createChatRoom(Client client, Client agent) {
        ChatRoom chatRoom = new ChatRoom(
                chatRoomIdCounter,
                true,
                agent,
                client,
                getTimeStamp(),
                "Room is active",
                new LinkedList<>()
        );
        chatRooms.put(chatRoomIdCounter, chatRoom);
        agent.setChatRoom(chatRoom);
        client.setChatRoom(chatRoom);
        chatRoomIdCounter++;
    }

    public static synchronized void closeChatRoom(ChatRoom chatRoom) {
        chatRoom.setChatRoomClosed(getTimeStamp());
        chatRoom.getClient().setChatRoom(null);
        chatRoom.getAgent().setChatRoom(null);
        chatRoom.setOpened(false);
    }
}
