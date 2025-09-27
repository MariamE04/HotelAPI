package app.mappers;

import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;

public class RoomMapper {
    public static RoomDTO toDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .number(room.getNumber())
                .price(room.getPrice())
                .build();
    }

    public static Room toEntity(RoomDTO dto, Hotel hotel) {
        Room room = new Room();
        room.setId(dto.getId());
        room.setNumber(dto.getNumber());
        room.setPrice(dto.getPrice());
        room.setHotel(hotel);
        return room;
    }
}
