package Server;

import Service.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Client {
    private String name;
    private Role role;
    private boolean isFree;
    private UserSocket connectedUserSocket;
}
