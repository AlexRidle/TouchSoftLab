package TouchSoftLabs.Dto;

import TouchSoftLabs.Enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private String name;
    private Role role;
    private boolean isFree;
    private String id;
    private String connectedId;
    private String chatRoomId;

}
