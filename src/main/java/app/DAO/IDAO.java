package app.DAO;

import app.entities.Hotel;
import app.entities.Room;
import java.util.List;


public interface IDAO {
    public List<Hotel> getAllHotels();

    public Hotel getHotelById(int id);

    public Hotel createHotel(Hotel hotel);

    public Hotel updateHotel(Hotel hotel);

    public boolean deleteHotel(int id);

    public Hotel addRoom(Hotel hotel, Room room);

    public boolean removeRoom(int roomId);

    public List<Room> getRoomsForHotel(int hotelId);
}
