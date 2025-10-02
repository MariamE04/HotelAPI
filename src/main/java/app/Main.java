package app;

import app.DAO.HotelDAO;
import app.Security.SecurityDAO;
import app.Security.rest.ISecurityDAO;
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.HotelDTO;
import app.entities.Hotel;
import app.exceptions.EntityNotFoundException;
import app.mappers.HotelMapper;
import app.utils.Populater;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws EntityNotFoundException {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        ISecurityDAO dao= new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        System.out.println("test");

        ApplicationConfig config = ApplicationConfig.getInstance();
        config.startServer(7071);       // opret Javalin først
        config.checkSecurityRoles();    // så kan vi tilføje beforeMatched handlers


        HotelDAO hotelDAO = new HotelDAO(emf);

        // få fake data som DTO’er
        List<HotelDTO> hotelDTOs = Populater.createHotels(new ArrayList<>());

        // mapper til entities og gem i DB
        for (HotelDTO dto : hotelDTOs) {
            Hotel hotel = HotelMapper.toEntity(dto);
            hotelDAO.createHotel(hotel);
        }

        dao.createUser("user2", "pass124");
        dao.createRole("USER");
        dao.addUserRole("user2", "USER");

    }

}