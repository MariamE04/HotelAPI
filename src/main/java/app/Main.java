package app;

import app.DAO.HotelDAO;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.entities.Hotel;
import app.mappers.HotelMapper;
import app.utils.Populater;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        System.out.println("test");

       // ApplicationConfig.startServer(7071);

        ApplicationConfig.getInstance().startServer(7071);

        HotelDAO hotelDAO = new HotelDAO(emf);

        // få fake data som DTO’er
        List<HotelDTO> hotelDTOs = Populater.createHotels(new ArrayList<>());

        // mapper til entities og gem i DB
        for (HotelDTO dto : hotelDTOs) {
            Hotel hotel = HotelMapper.toEntity(dto);
            hotelDAO.createHotel(hotel);
        }

    }

}