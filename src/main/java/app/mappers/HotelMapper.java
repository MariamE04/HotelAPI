package app.mappers;

import app.dtos.HotelDTO;
import app.entities.Hotel;

public class HotelMapper {

        public static HotelDTO toDTO(Hotel hotel) {
            return HotelDTO.builder()
                    .id(hotel.getId())
                    .name(hotel.getName())
                    .address(hotel.getAddress())
                    .rooms(
                            hotel.getRooms().stream()
                                    .map(RoomMapper::toDTO)
                                    .toList()
                    )
                    .build();
        }

        public static Hotel toEntity(HotelDTO dto) {
            Hotel hotel = new Hotel();
            hotel.setId(dto.getId());
            hotel.setName(dto.getName());
            hotel.setAddress(dto.getAddress());

            // Hvis du vil sætte rooms på
            if (dto.getRooms() != null) {
                hotel.setRooms(
                        dto.getRooms().stream().map(r -> RoomMapper.toEntity(r, hotel)) // pas hotel ind
                                .toList()
                );
            }
            return hotel;
        }
}
