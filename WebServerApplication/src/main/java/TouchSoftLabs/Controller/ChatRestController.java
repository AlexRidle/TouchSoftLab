package TouchSoftLabs.Controller;

import TouchSoftLabs.Converter.ChatRoomConverter;
import TouchSoftLabs.Converter.ClientConverter;
import TouchSoftLabs.Dto.ChatRoomDto;
import TouchSoftLabs.Dto.ClientDto;
import TouchSoftLabs.Entity.ChatRoom;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Enumeration.Role;
import TouchSoftLabs.Server.WebServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@RequestMapping("/api")
@RestController
public class ChatRestController {

    private final ClientConverter clientConverter;
    private final ChatRoomConverter chatRoomConverter;
    private static final HashMap<String, Client> users = WebServerEndpoint.getUsers();
    private static final LinkedList<String> usersQueue = WebServerEndpoint.getUsersQueue();
    private static final HashMap<Integer, ChatRoom> chatRooms = WebServerEndpoint.getChatRooms();

    @Autowired
    public ChatRestController(final ClientConverter clientConverter, final ChatRoomConverter chatRoomConverter) {
        this.clientConverter = clientConverter;
        this.chatRoomConverter = chatRoomConverter;
    }

    @GetMapping("/info")
    public String getInfo() {
        return "TouchSoftLab Task 4.";
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
        jsonObject.put("freeAgents", counter);
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

    @GetMapping("/agentDetails")
    public ClientDto getAgentDetails(@RequestParam(name = "id") String id) {
        Client agent = users.get(id);
        ClientDto dto = null;
        if (agent != null && agent.getRole() == Role.AGENT) {
            dto = clientConverter.convertToDto(agent);
        }
        return dto;
    }

    @GetMapping("/clientDetails")
    public ClientDto getClientDetails(@RequestParam(name = "id") String id) {
        Client client = users.get(id);
        ClientDto dto = null;
        if (client != null && client.getRole() == Role.CLIENT) {
            dto = clientConverter.convertToDto(client);
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
    public ChatRoomDto getChatRoomDetails(@RequestParam(name = "id") int id) {
        ChatRoom chatRoom = chatRooms.get(id);
        ChatRoomDto chatRoomDto = null;
        if (chatRoom != null) {
            chatRoomDto = chatRoomConverter.convertToDto(chatRoom);
        }
        return chatRoomDto;
    }


}
