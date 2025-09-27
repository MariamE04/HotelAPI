package app.dtos;

import app.entities.Room;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {
    private int id;
    private String name;
    private String address;
    private List<RoomDTO> rooms;

}
