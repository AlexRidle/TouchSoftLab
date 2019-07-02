package TouchSoftLabs.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    private int id;
    private boolean isOpened;
    private Client agent;
    private Client client;
    private String chatRoomCreated;
    private String chatRoomClosed;
    private LinkedList<Message> messageList;

}
