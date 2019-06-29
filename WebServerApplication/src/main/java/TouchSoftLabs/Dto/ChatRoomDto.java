package TouchSoftLabs.Dto;

import TouchSoftLabs.Entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {

    private int id;
    private boolean isOpened;
    private String agentId;
    private String clientId;
    private String chatRoomCreated;
    private String chatRoomClosed;
    private LinkedList<Message> messageList;

}
