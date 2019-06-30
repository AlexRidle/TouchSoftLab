package TouchSoftLabs.Converter;

import TouchSoftLabs.Dto.ClientDto;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.WebSocket.WebServerEndpoint;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class ClientConverter implements EntityConverter<ClientDto, Client> {

    @Override
    public ClientDto convertToDto(final Client entity) {
        final ClientDto dto = new ClientDto();
        BeanUtils.copyProperties(entity, dto, "session", "connectedSession", "queuedMessages", "chatRoom");
        dto.setId(entity.getSession().getId());
        dto.setConnectedId(
                entity.getConnectedSession() != null ?
                        entity.getConnectedSession().getId() :
                        "unconnected"
        );
        dto.setChatRoomId(
                entity.getChatRoom() != null ?
                        String.valueOf(entity.getChatRoom().getId()) :
                        "unconnected"
        );
        return dto;
    }

    @Override
    public Client convertToEntity(final ClientDto dto) {
        final Client entity = new Client();
        BeanUtils.copyProperties(entity, dto, "id", "connectedId", "chatRoomId");
        entity.setSession(WebServerEndpoint.getUsers().get(dto.getId()).getSession());
        entity.setConnectedSession(WebServerEndpoint.getUsers().get(dto.getConnectedId()).getConnectedSession());
        entity.setQueuedMessages(new LinkedList<>());
        entity.setChatRoom(WebServerEndpoint.getChatRooms().get(Integer.valueOf(dto.getChatRoomId())));
        return entity;
    }

}