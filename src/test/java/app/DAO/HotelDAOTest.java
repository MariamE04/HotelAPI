package app.DAO;

import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelDAOTest {
    private static EntityManagerFactory emf;
    private static HotelDAO dao;

    // Gem IDs på testhoteller, så vi kan bruge dem i tests
    private int hotel1Id;
    private int hotel2Id;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = new HotelDAO(emf);
    }

    @AfterAll
    static void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setUpTestData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();

            Hotel h1 = Hotel.builder()
                    .name("Hotel 1")
                    .address("Address 1")
                    .build();

            Room r1 = new Room(null, h1, 101, 100.0);
            Room r2 = new Room(null, h1, 102, 150.0);

            h1.setRooms(new ArrayList<>(java.util.List.of(r1, r2)));

            em.persist(h1);

            Hotel h2 = Hotel.builder()
                    .name("Hotel 2")
                    .address("Address 1")
                    .build();

            Room r3 = new Room(null, h2, 140, 104.0);
            Room r4 = new Room(null, h2, 150, 103.0);

            h2.setRooms(new ArrayList<>(java.util.List.of(r3, r4)));

            em.persist(h2);


            tx.commit();

            // Gem ID’erne
            hotel1Id = h1.getId();
            hotel2Id = h2.getId();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    @Test
    void getAllHotels() {
        List<Hotel> hotels = dao.getAllHotels();

        assertNotNull(hotels);
        assertEquals(2, hotels.size());

        // Tjek at de rigtige navne er der
        List<String> names = hotels.stream().map(Hotel::getName).toList();
        assertTrue(names.contains("Hotel 1"));
        assertTrue(names.contains("Hotel 2"));

    }

    @Test
    void getHotelById() {
        Hotel found = dao.getHotelById(hotel1Id);
        assertNotNull(found);
        assertEquals("Hotel 1", found.getName());
        assertEquals(2, found.getRooms().size());
    }

    @Test
    void createHotel() {
        Hotel h3 = Hotel.builder()
                .name("Hotel 3")
                .address("Address 3")
                .build();

        Room r3 = new Room(null, h3, 100, 104.0);
        Room r4 = new Room(null, h3, 120, 103.0);

        h3.setRooms(new ArrayList<>(java.util.List.of(r3, r4)));

        dao.createHotel(h3);

        Hotel created = dao.getHotelById(h3.getId());

        assertNotNull(created);
        assertEquals("Hotel 3", created.getName());

    }

    @Test
    void updateHotel() {
      Hotel h1 =  dao.getHotelById(hotel1Id);

      h1.setName("test 1");;

      dao.updateHotel(h1);

      Hotel found = dao.getHotelById(h1.getId());
      assertEquals("test 1", found.getName());

    }

    @Test
    void deleteHotel() {

        // Før sletning burde der være 2
        assertEquals(2, dao.getAllHotels().size());

        dao.deleteHotel(hotel2Id);

        // Efter sletning burde der være 1
        assertEquals(1, dao.getAllHotels().size());
    }

    @Test
    void addRoom() {
        Hotel h1 = dao.getHotelById(hotel1Id);
        Room r1 = new Room(null, h1, 200, 143.0);

        h1 = dao.addRoom(h1, r1); // Brug det returned hotel

        assertEquals(3, h1.getRooms().size());

    }

    @Test
    void removeRoom() {
        // Hent hotel og et rum
        Hotel h1 = dao.getHotelById(hotel1Id);
        Room r1 = h1.getRooms().get(0); // vælg første rum
        int roomId = r1.getId();

        // Slet rummet
        boolean removed = dao.removeRoom(roomId);
        assertTrue(removed);

        // Tjek at rummet er væk fra hotellets liste
        h1 = dao.getHotelById(hotel1Id);
        assertEquals(1, h1.getRooms().size());
        assertFalse(h1.getRooms().stream().anyMatch(r -> r.getId() == roomId));

        // Tjek at rummet ikke findes i DB
        assertNull(dao.getRoomById(roomId));
    }

    @Test
    void getRoomsForHotel() {
        List<Room> rooms = dao.getRoomsForHotel(hotel1Id);

        assertNotNull(rooms);
        assertEquals(2, rooms.size());

        List<Integer> roomNumbers = rooms.stream().map(Room::getNumber).toList();
        assertTrue(roomNumbers.contains(101));
        assertTrue(roomNumbers.contains(102));

        // Test med et hotel der ikke findes
        List<Room> emptyRooms = dao.getRoomsForHotel(9999);
        assertNotNull(emptyRooms);
        assertTrue(emptyRooms.isEmpty());
    }

    @Test
    void getRoomById() {
        Hotel h1 = dao.getHotelById(hotel1Id);
        Room r1 = h1.getRooms().get(0);

        Room found = dao.getRoomById(r1.getId());
        assertNotNull(found);
        assertEquals(r1.getNumber(), found.getNumber());
        assertEquals(r1.getPrice(), found.getPrice());

        // Test med et rum der ikke findes
        assertNull(dao.getRoomById(9999));
    }
}