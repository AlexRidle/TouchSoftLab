package TouchSoftLabs.Entity;

import TouchSoftLabs.Enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.websocket.Session;
import java.util.LinkedList;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    private String name;
    private Role role;
    private boolean isFree;
    private Session session;
    private Session connectedSession;
    private ChatRoom chatRoom;
    private LinkedList<Message> queuedMessages;

}
