package Server;

import Service.Role;
import Service.ServerLogger;

import java.util.HashMap;
import java.util.HashSet;

class ClientService {

    void registerClient(String input, UserSocket userSocket) {
        HashMap<String, String> clientInfo = convertStringToHashMap(input);
        if (clientInfo.get("role").equalsIgnoreCase(Role.AGENT.toString())) {
            userSocket.setClient(getNewClient(clientInfo.get("name"), Role.AGENT, true));
        } else {
            userSocket.setClient(getNewClient(clientInfo.get("name"), Role.valueOf(clientInfo.get("role").toUpperCase()), false));
        }

        try {
            userSocket.send(
                    String.format("Вы подключились под именем \"%s\"", userSocket.getClient().getName()),
                    userSocket.getSocketOut()
            );
            ServerLogger.logInfo(String.format("Пользователь \"%s\" подключился к чату", userSocket.getClient().getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Client getNewClient(String name, Role role, boolean isFree) {
        Client client = new Client();
        client.setName(name);
        client.setRole(role);
        client.setFree(isFree);
        return client;
    }

    private HashMap<String, String> convertStringToHashMap(String input) {
        HashMap<String, String> content = new HashMap<>();
        input = input.substring(1, input.length() - 1);
        String[] pairs = input.split(", ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            content.put(keyValue[0], keyValue[1]);
        }
        return content;
    }

    void tryFindFreeAgent(final HashSet<UserSocket> userSockets, final UserSocket userSocket, final String message) {
        UserSocket agent = getFreeAgent(userSockets);
        userSocket.getClient().setConnectedUserSocket(agent);

        if(agent == null){
            userSocket.send("На данный момент все агенты заняты. Попробуйте повторить отправку сообщения позднее", userSocket.getSocketOut());
            ServerLogger.logWarn(String.format("Пользователь \"%s\" пытался установить соединение, но не нашел свободных агентов", userSocket.getClient().getName()));
        } else {
            agent.getClient().setConnectedUserSocket(userSocket);
            userSocket.send(
                    String.format("Вы были подключены к агенту %s", agent.getClient().getName()),
                    userSocket.getSocketOut()
            );
            agent.send(
                    String.format("К вам подключился пользователь %s", userSocket.getClient().getName()),
                    agent.getSocketOut()
            );
            ServerLogger.logInfo(String.format("Пользователь \"%s\" соединился с агентом \"%s\"", userSocket.getClient().getName(), agent.getClient().getName()));
            agent.send(message, agent.getSocketOut());

        }
    }

    private UserSocket getFreeAgent(HashSet<UserSocket> userSockets){
        for(UserSocket userSocket : userSockets){
            if(userSocket.getClient().getRole() == Role.AGENT && userSocket.getClient().isFree()){
                userSocket.getClient().setFree(false);
                return userSocket;
            }
        }
        return null;
    }

    void leave(final UserSocket userSocket) {
        Client client = userSocket.getClient();
        if(client.getConnectedUserSocket() != null){
            userSocket.send("Вы вышли из диалога.", userSocket.getSocketOut());
            client.getConnectedUserSocket().send(
                    String.format("%s вышел из диалога.", client.getName()),
                    client.getConnectedUserSocket().getSocketOut()
            );
            ServerLogger.logInfo(String.format("Пользователь \"%s\" отсоединился от пользователя \"%s\"", client.getName(), client.getConnectedUserSocket().getClient().getName()));
            client.getConnectedUserSocket().getClient().setConnectedUserSocket(null);
            client.getConnectedUserSocket().getClient().setFree(true);
            client.setConnectedUserSocket(null);
            client.setFree(true);
        } else {
            userSocket.send("Команда не выполнена. Вы не подключены к собеседнику.", userSocket.getSocketOut());
        }
    }
}

