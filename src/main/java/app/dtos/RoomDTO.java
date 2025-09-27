package app.dtos;

import lombok.*;

@Data
@Builder
public class RoomDTO {
    private Integer id;
    private Integer hotelId;
    private int number;
    private double price;


    public RoomDTO(Integer id, Integer hotelId, int number, double price) {
        this.id = id;
        this.hotelId = hotelId;
        this.number = number;
        this.price = price;
    }
}
