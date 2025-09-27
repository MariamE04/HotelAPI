package app.DAO;

import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
    public Hotel removeRoom(Hotel hotel, Room room) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Hotel managedHotel = em.find(Hotel.class, hotel.getId());
            Room managedRoom = em.find(Room.class, room.getId());

            if (managedHotel != null && managedRoom != null) {
                managedHotel.getRooms().remove(managedRoom);
                em.remove(managedRoom);
            }

            em.getTransaction().commit();
            return managedHotel;
        }
    }

    @Override
    public List<Room> getRoomsForHotel(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel managedHotel = em.find(Hotel.class, hotel.getId());
            return managedHotel.getRooms();
        }
    }

}
