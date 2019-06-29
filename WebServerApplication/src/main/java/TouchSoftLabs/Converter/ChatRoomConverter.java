package TouchSoftLabs.Converter;

import TouchSoftLabs.Dto.ChatRoomDto;
import TouchSoftLabs.Dto.ClientDto;
import TouchSoftLabs.Entity.ChatRoom;
import TouchSoftLabs.Entity.Client;
import TouchSoftLabs.Server.WebServerEndpoint;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class ChatRoomConverter implements EntityConverter<ChatRoomDto, ChatRoom> {

    @Override
    public ChatRoomDto convertToDto(final ChatRoom entity) {
        final ChatRoomDto dto = new ChatRoomDto();
        BeanUtils.copyProperties(entity, dto, "client", "agent");
        dto.setClientId(entity.getClient().getSession().getId());
        dto.setAgentId(entity.getAgent().getSession().getId());
        return dto;
    }

    @Override
    public ChatRoom convertToEntity(final ChatRoomDto dto) {
        final ChatRoom entity = new ChatRoom();
        BeanUtils.copyProperties(entity, dto, "clientId", "agentId");
        entity.setAgent(WebServerEndpoint.getUsers().get(dto.getAgentId()));
        entity.setClient(WebServerEndpoint.getUsers().get(dto.getClientId()));
        return entity;
    }

}