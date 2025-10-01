package app.DAO;

import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class HotelDAO implements IDAO{
    private final EntityManagerFactory emf;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Hotel> getAllHotels() {
        try(EntityManager em = emf.createEntityManager()){
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h", Hotel.class)
                    .getResultList();

            return hotels;
        }
    }

    @Override
    public Hotel getHotelById(int id) {
        try(EntityManager em = emf.createEntityManager()){
            return em.find(Hotel.class, id);
        }
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.persist(hotel);

            em.getTransaction().commit();

            return hotel;
        }
    }

    @Override
    public Hotel updateHotel(Hotel hotel) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            Hotel updated = em.merge(hotel);

            em.getTransaction().commit();

            return updated;
        }
    }

    @Override
    public boolean deleteHotel(int id) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            Hotel delete = em.find(Hotel.class, id);
            if(delete != null){
                em.remove(delete);
                em.getTransaction().commit();
                return true;
            }  else {
                em.getTransaction().rollback();
                return false;
            }
        }
    }

    @Override
    public Hotel addRoom(Hotel hotel, Room room) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Hotel managedHotel = em.find(Hotel.class, hotel.getId());

            room.setHotel(managedHotel);  // vigtigt: sæt ejerskabet
            managedHotel.getRooms().add(room);

            em.persist(room);  // persister room
            em.getTransaction().commit();

            return managedHotel;
        }
    }

    @Override
    public boolean removeRoom(int roomId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Room room = em.find(Room.class, roomId);
            if (room != null) {
                Hotel hotel = room.getHotel(); // hent hotel
                if (hotel != null) {
                    hotel.getRooms().remove(room); // fjern fra hotel
                }
                em.remove(room); // slet room
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        }
    }

    @Override
    public List<Room> getRoomsForHotel(int hotelId) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel hotel1 = em.find(Hotel.class, hotelId);
            if (hotel1 != null) {
                return hotel1.getRooms();
            } else {
                return List.of(); // returner tom liste hvis hotel ikke findes
            }
        }
    }

    public Room getRoomById(int roomId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Room.class, roomId);
        }
    }


}
