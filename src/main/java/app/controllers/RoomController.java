package app.controllers;

import app.DAO.HotelDAO;
import app.config.HibernateConfig;
import app.dtos.RoomDTO;
import app.entities.Room;
import app.mappers.RoomMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class RoomController{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private HotelDAO dao = new HotelDAO(emf);

    public void roomToDelete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = dao.removeRoom(id);

        if (deleted) {
            ctx.result("Room with id " + id + " deleted");
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.result("Room not found");
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    public void getRoomById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        Room room = dao.getRoomById(id); // henter Room entity
        if (room != null) {
            // Brug RoomMapper til at lave en DTO
            RoomDTO roomDTO = RoomMapper.toDTO(room);
            ctx.json(roomDTO);       // returner DTO i stedet for entity
            ctx.status(HttpStatus.OK);
        } else {
            ctx.result("Room not found");
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }


    public void getRoomsForHotel(Context ctx) {
        int hotelId = Integer.parseInt(ctx.pathParam("hotelId"));
        List<Room> rooms = dao.getRoomsForHotel(hotelId);

        // mapper til DTO
        List<RoomDTO> roomDTOs = rooms.stream()
                .map(RoomMapper::toDTO)
                .toList();
        ctx.json(roomDTOs);
    }


}
