package app.utils;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;

import java.util.ArrayList;
import java.util.List;

public class Populater {

    public static List<HotelDTO> createHotels(List<HotelDTO> hotelList) {

        // Hotel 1 med 2 rooms
        HotelDTO h1 = HotelDTO.builder()
                .id(1)
                .name("Hotel 1")
                .address("Address 1")
                .rooms(new ArrayList<>(List.of(
                        new RoomDTO(1, 1, 101, 100.0),
                        new RoomDTO(2, 1, 102, 150.0)
                )))
                .build();

        // Hotel 2 med 1 room
        HotelDTO h2 = HotelDTO.builder()
                .id(2)
                .name("Hotel 2")
                .address("Address 2")
                .rooms(new ArrayList<>(List.of(
                        new RoomDTO(3, 2, 201, 200.0)
                )))
                .build();

        // Hotel 3 uden rooms
        HotelDTO h3 = HotelDTO.builder()
                .id(3)
                .name("Hotel 3")
                .address("Address 3")
                .rooms(new ArrayList<>())
                .build();

        hotelList.add(h1);
        hotelList.add(h2);
        hotelList.add(h3);

        return hotelList;
    }

    public static List<RoomDTO> createRooms(List<RoomDTO> roomList) {
        RoomDTO r1 = new RoomDTO(1, 1, 101, 100.0);
        RoomDTO r2 = new RoomDTO(2, 1, 102, 150.0);
        RoomDTO r3 = new RoomDTO(3, 2, 201, 200.0);

        roomList.add(r1);
        roomList.add(r2);
        roomList.add(r3);

        return roomList;
    }
}
