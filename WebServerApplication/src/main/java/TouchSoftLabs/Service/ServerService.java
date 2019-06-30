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
            sendMessageFromServerToUser(String.format("Вы были подключены к агенту %s", agent.getName()), client);
            sendMessageFromServerToUser(String.format("К вам подключился пользователь %s", client.getName()), agent);
            ServerLogger.logInfo(String.format("Пользователь \"%s\" соединился с агентом \"%s\"", client.getName(), agent.getName()));

            createChatRoom(client, agent);
            return true;
        }
    }

    static private synchronized void storeMessage(final Client client, final Message message) throws IOException, EncodeException {
        if (!usersQueue.contains(client.getSession().getId())) {
            sendMessageFromServerToUser("На данный момент все агенты заняты. На ваше сообщение ответит первый освободившийся агент", client);
            usersQueue.add(client.getSession().getId());
        }

        sendMessageFromServerToUser(
                "Сообщение записано. Ваша позиция в очереди: " + (usersQueue.indexOf(client.getSession().getId()) + 1),
                client
        );

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
                    sendMessageFromServerToUser("Собеседник отсутствует.", client);
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
            sendMessageFromServerToUser(String.format("%s завершил диалог.", client.getName()),connectedClient);

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

            sendMessageFromServerToUser("Вы вышли из диалога", client);
            sendMessageFromServerToUser(String.format("%s вышел из диалога.", client.getName()), connectedClient);

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
            sendMessageFromServerToUser("Вы покинули очередь. История ваших сообщений очищена.", client);
            return true;
        } else {
            sendMessageFromServerToUser("Команда не выполнена. Вы не подключены к собеседнику.", client);
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

                sendMessageFromServerToUser(String.format("К вам подключился пользователь %s. Пропущенные сообщения:", client.getName()), agent);
                for (Message message : client.getQueuedMessages()) {
                    sendMessage(message, agent);
                    client.getChatRoom().getMessageList().add(message);
                }
                client.getQueuedMessages().clear();

                if (usersQueue.size() > 0) {
                    sendMessageFromServerToUser(String.format("Количество пользователей ожидающих ответа: %s", usersQueue.size()),agent);
                }
                sendMessageFromServerToUser( String.format("Вы были подключены к агенту %s. Ваши ранее написанные сообщения были отправлены.", agent.getName()), client);

                notifyQueuedUsersFromIndex(0);
                ServerLogger.logInfo(String.format("Пользователь \"%s\" соединился с агентом \"%s\"", client.getName(), agent.getName()));
            }


        }

    }

    static private synchronized void notifyQueuedUsersFromIndex(int fromIndex) throws IOException, EncodeException {
        Client client;
        for (int index = fromIndex; index < usersQueue.size() && index != -1; index++) {
            client = users.get(usersQueue.get(index));
            sendMessageFromServerToUser("Ваша позиция в очереди: " + (index + 1), client);
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

    private static synchronized void sendMessageFromServerToUser(String serverMessage, Client userTo) throws IOException, EncodeException {
        Message message = new Message("SERVER", serverMessage, getTimeStamp());
        sendMessage(message, userTo);
    }
}
