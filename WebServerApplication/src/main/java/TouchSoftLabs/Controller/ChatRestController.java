package TouchSoftLabs.Controller;

import TouchSoftLabs.Converter.ChatRoomConverter;
import TouchSoftLabs.Converter.ClientConverter;
import TouchSoftLabs.Dto.ChatRoomDto;
import TouchSoftLabs.Dto.ClientDto;
import TouchSoftLabs.Entity.ChatRoom;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Enumeration.Role;
import TouchSoftLabs.Service.HttpService;
import TouchSoftLabs.WebSocket.WebHttpClient;
import TouchSoftLabs.WebSocket.WebServerEndpoint;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;

@RequestMapping("/api")
@RestController
public class ChatRestController {

    private final ClientConverter clientConverter;
    private final ChatRoomConverter chatRoomConverter;
    private final HttpService httpService;

    private static final HashMap<String, Client> users = WebServerEndpoint.getUsers();
    private static final LinkedList<String> usersQueue = WebServerEndpoint.getUsersQueue();
    private static final HashMap<Integer, ChatRoom> chatRooms = WebServerEndpoint.getChatRooms();

    @Autowired
    public ChatRestController(final ClientConverter clientConverter, final ChatRoomConverter chatRoomConverter, final HttpService httpService) {
        this.clientConverter = clientConverter;
        this.chatRoomConverter = chatRoomConverter;
        this.httpService = httpService;
    }

    @GetMapping("/freeAgentsNum")
    public String getFreeAgentsNum() {
        JSONObject jsonObject = new JSONObject();
        int counter = 0;
        for (Client client : users.values()) {
            if (client.getRole() == Role.AGENT && client.isFree()) {
                counter++;
            }
        }
        jsonObject.put("freeAgentsNum", counter);
        return jsonObject.toString();
    }

    @GetMapping("/freeAgents")
    public String getFreeAgents() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (Client client : users.values()) {
            if (client.getRole() == Role.AGENT && client.isFree()) {
                JSONObject jsonAgent = new JSONObject();
                jsonAgent.put("id", client.getSession().getId());
                jsonAgent.put("name", client.getName());
                jsonArray.put(jsonAgent);
            }
        }
        jsonObject.put("freeAgents", jsonArray);
        return jsonObject.toString();
    }

    @GetMapping("/registeredAgents")
    public String getRegisteredAgents() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (Client client : users.values()) {
            if (client.getRole() == Role.AGENT) {
                JSONObject jsonAgent = new JSONObject();
                jsonAgent.put("id", client.getSession().getId());
                jsonAgent.put("name", client.getName());
                jsonArray.put(jsonAgent);
            }
        }
        jsonObject.put("registeredAgents", jsonArray);
        return jsonObject.toString();
    }

    @GetMapping("/registeredClients")
    public String getRegisteredClients() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (Client client : users.values()) {
            if (client.getRole() == Role.CLIENT) {
                JSONObject jsonClient = new JSONObject();
                jsonClient.put("id", client.getSession().getId());
                jsonClient.put("name", client.getName());
                jsonArray.put(jsonClient);
            }
        }
        jsonObject.put("registeredClients", jsonArray);
        return jsonObject.toString();
    }

    @GetMapping("/agentDetails")
    public ClientDto getAgentDetails(@RequestParam(name = "id") String id, HttpServletResponse response) {
        Client agent = users.get(id);
        ClientDto dto = null;
        if (agent != null && agent.getRole() == Role.AGENT) {
            dto = clientConverter.convertToDto(agent);
        } else {
            response.setStatus(204);
        }
        return dto;
    }

    @GetMapping("/clientDetails")
    public ClientDto getClientDetails(@RequestParam(name = "id") String id, HttpServletResponse response) {
        Client client = users.get(id);
        ClientDto dto = null;
        if (client != null && client.getRole() == Role.CLIENT) {
            dto = clientConverter.convertToDto(client);
        } else {
            response.setStatus(204);
        }
        return dto;
    }

    @GetMapping("/queuedClients")
    public String getQueuedClients() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        Client client;
        for (int index = 0; index < usersQueue.size(); index++) {
            client = users.get(usersQueue.get(index));
            JSONObject jsonClient = new JSONObject();
            jsonClient.put("queuePosition", index + 1);
            jsonClient.put("id", client.getSession().getId());
            jsonClient.put("name", client.getName());
            jsonArray.put(jsonClient);
        }
        jsonObject.put("queuedClients", jsonArray);
        return jsonObject.toString();
    }

    @GetMapping("/chatRooms")
    public String getChatRooms(@RequestParam(name = "closed", required = false) String closed) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (ChatRoom chatRoom : chatRooms.values()) {
            if ((chatRoom.isOpened() && closed == null) ||
                    (!chatRoom.isOpened() && closed != null)) {
                JSONObject jsonChatRoom = new JSONObject();
                jsonChatRoom.put("id", chatRoom.getId());
                jsonChatRoom.put("agent", chatRoom.getAgent().getName());
                jsonChatRoom.put("client", chatRoom.getClient().getName());
                jsonArray.put(jsonChatRoom);
            }
        }
        jsonObject.put("chatRooms", jsonArray);
        return jsonObject.toString();
    }

    @GetMapping("/chatRoomDetails")
    public ChatRoomDto getChatRoomDetails(@RequestParam(name = "id") int id, HttpServletResponse response) {
        ChatRoom chatRoom = chatRooms.get(id);
        ChatRoomDto chatRoomDto = null;
        if (chatRoom != null) {
            chatRoomDto = chatRoomConverter.convertToDto(chatRoom);
        } else {
            response.setStatus(204);
        }
        return chatRoomDto;
    }

    @PostMapping("/registerClient")
    public String registerClient(@RequestParam(value = "name") String name) throws URISyntaxException {
        JSONObject jsonObject = new JSONObject();
        WebSocketClient client = new WebHttpClient(new URI(String.format("ws://localhost:8080/%s/%s/", "client", name)));
        client.connect();
        jsonObject.put("response", "Successfully registered CLIENT with name " + name);
        return jsonObject.toString();
    }

    @PostMapping("/registerAgent")
    public String registerAgent(@RequestParam(value = "name") String name) throws URISyntaxException {
        JSONObject jsonObject = new JSONObject();
        WebSocketClient agent = new WebHttpClient(new URI(String.format("ws://localhost:8080/%s/%s/", "agent", name)));
        agent.connect();
        jsonObject.put("response", "Successfully registered AGENT with name " + name);
        return jsonObject.toString();
    }

    @PostMapping("/sendMessageToClient")
    public String sendMessageToClient(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "agentId") String agentId) throws IOException, EncodeException {
        JSONObject jsonObject = new JSONObject();
        Client agent = users.get(agentId);
        if (agent != null && agent.getRole() == Role.AGENT) {
            httpService.sendMessage(users.get(agentId), message);
            jsonObject.put("response", "Message has been successfully sent");
        } else {
            jsonObject.put("response", "Message has not been sent");
        }
        return jsonObject.toString();
    }

    @PostMapping("/sendMessageToAgent")
    public String sendMessageToAgent(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "clientId") String clientId) throws IOException, EncodeException {
        JSONObject jsonObject = new JSONObject();
        Client client = users.get(clientId);
        if (client != null && client.getRole() == Role.CLIENT) {
            httpService.sendMessage(users.get(clientId), message);
            jsonObject.put("response", "Message has been successfully sent");
        } else {
            jsonObject.put("response", "Message has not been sent");
        }
        return jsonObject.toString();
    }

    @GetMapping("/receiveMessages")
    public String receiveMessages(
            @RequestParam(value = "userId") String userId) throws InterruptedException {
        JSONObject jsonObject = new JSONObject();
        WebHttpClient webHttpClient = WebHttpClient.getWebSocketClients().get(userId);
        if (webHttpClient != null) {
            return webHttpClient.convertListToStringAndClearNewMessages();
        }
        jsonObject.put("response", "Can not load new massages of user with id " + userId);
        return jsonObject.toString();
    }

    @PostMapping("/leaveChatRoom")
    public String leaveChatRoom(
            @RequestParam(value = "userId") String userId) throws IOException, EncodeException {
        JSONObject jsonObject = new JSONObject();
        Client client = users.get(userId);
        if (client != null && httpService.disconnect(client)) {
            jsonObject.put("response", "Successfully disconnected user with id " + userId);
        } else {
            jsonObject.put("response", "Can not disconnect user with id " + userId);
        }
        return jsonObject.toString();
    }

    @PostMapping("/closeConnectionOfUser")
    public String closeConnectionOfUser(
            @RequestParam(value = "userId") String userId) throws IOException {
        JSONObject jsonObject = new JSONObject();
        Client client = users.get(userId);
        if (client != null) {
            client.getSession().close();
            jsonObject.put("response", "Successfully closed connection with user id " + userId);
        } else {
            jsonObject.put("response", "Can not close connection with user id " + userId);
        }
        return jsonObject.toString();
    }
}
