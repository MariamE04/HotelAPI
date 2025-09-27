package app.controllers;

import app.DAO.HotelDAO;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.entities.Hotel;
import app.mappers.HotelMapper;
import app.mappers.RoomMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class HotelController {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private HotelDAO dao = new HotelDAO(emf);

    public void getAllHotels(Context ctx){
        List<Hotel> hotels = dao.getAllHotels();
        List<HotelDTO> hotelDTOS = hotels.stream().map(HotelMapper::toDTO).toList();
        ctx.status(HttpStatus.OK);
        ctx.json(hotelDTOS);
    }

    public void getHotelById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Hotel hotel = dao.getHotelById(id);
        if(hotel != null){
            ctx.status(200);
            ctx.json(HotelMapper.toDTO(hotel));
        }  else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Hotel not found");
        }
    }

    public void createHotel(Context ctx){
        String body = ctx.body().trim();
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<HotelDTO> dtos;

            // Tjek om det er en liste eller enkelt objekt
            if (body.startsWith("[")) {
                dtos = mapper.readValue(body, new TypeReference<List<HotelDTO>>() {});
            } else {
                dtos = List.of(mapper.readValue(body, HotelDTO.class));
            }

            List<HotelDTO> saved = new ArrayList<>();
            for (HotelDTO dto : dtos) {
                // Opret hotel entity uden rooms først
                Hotel hotel = Hotel.builder()
                        .name(dto.getName())
                        .address(dto.getAddress())
                        .rooms(new ArrayList<>()) // tom liste
                        .build();

                Hotel persistedHotel = dao.createHotel(hotel);

                // Hvis DTO har rooms, konverter og tilføj
                if (dto.getRooms() != null) {
                    for (var roomDTO : dto.getRooms()) {
                        var room = RoomMapper.toEntity(roomDTO, persistedHotel);
                        dao.addRoom(persistedHotel, room); // sætter ejerskab
                    }
                }

                // Tilføj til resultat som DTO
                saved.add(HotelMapper.toDTO(persistedHotel));
            }

            if (saved.size() == 1) {
                ctx.status(201).json(saved.get(0));
            } else {
                ctx.status(201).json(saved);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Invalid JSON input: " + e.getMessage(), e);
        }
    }

    public void updateHotel(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            HotelDTO dto = ctx.bodyAsClass(HotelDTO.class);

            // Hent eksisterende hotel
            Hotel existingHotel = dao.getHotelById(id);
            if (existingHotel == null) {
                ctx.status(HttpStatus.NOT_FOUND);
                ctx.result("Hotel not found");
                return;
            }

            // Opdater navn og adresse
            existingHotel.setName(dto.getName());
            existingHotel.setAddress(dto.getAddress());

            // Opdater rooms, hvis DTO indeholder nogen
            if (dto.getRooms() != null) {
                for (var roomDTO : dto.getRooms()) {
                    // Konverter RoomDTO til Room entity med ejerskab
                    var room = RoomMapper.toEntity(roomDTO, existingHotel);

                    // Tilføj room via DAO (håndterer persist og relation)
                    dao.addRoom(existingHotel, room);
                }
            }

            Hotel updated = dao.updateHotel(existingHotel);

            ctx.status(HttpStatus.OK);
            ctx.json(HotelMapper.toDTO(updated));

        } catch (Exception e) {
            throw new IllegalStateException("Invalid JSON input: " + e.getMessage(), e);
        }
    }

}
