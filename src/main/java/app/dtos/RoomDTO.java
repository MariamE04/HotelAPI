package app.dtos;

import lombok.*;

@Data
@Builder
public class RoomDTO {
    private int id;
    private int hotelId;
    private int number;
    private double price;


    public RoomDTO(int id, int hotelId, int number, double price) {
        this.id = id;
        this.hotelId = hotelId;
        this.number = number;
        this.price = price;
    }
}
