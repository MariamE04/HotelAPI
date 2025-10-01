import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoomControllerTest {

    private Javalin app;
    private EntityManagerFactory emf;

    @BeforeAll
    void setup() {
        // Start test db
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // Start javalin server
        ApplicationConfig.getInstance().startServer(7071);
        app = ApplicationConfig.getInstance().getApp();

        // Konfigurer RestAssured base URI
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7071;
        RestAssured.basePath = "/api/hotels";

    }

    @AfterAll
    void tearDown() {
        // Stop server
        ApplicationConfig.getInstance().stopServer();

        // Luk emf
        if(emf != null){
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

            Hotel hotel = Hotel.builder()
                    .name("Hotel Test")
                    .address("Test Address")
                    .build();
            em.persist(hotel);

            Room r1 = new Room(null, hotel, 101, 100.0);
            Room r2 = new Room(null, hotel, 102, 150.0);
            em.persist(r1);
            em.persist(r2);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    @Test
    void getRoomById() {
        given()
                .when()
                .get("/room/1")  // basePath = "/api/hotels" => /api/hotels/room/1
                .then()
                .statusCode(200)
                .body("number", equalTo(101))
                .body("price", equalTo(100.0f));
    }


    @Test
    void getRoomsForHotel() {
        given()
                .when()
                .get("/room/1/rooms")  // basePath = "/api/hotels" => /api/hotels/room/1/rooms
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].number", is(101))
                .body("[0].price", is(100.0f))
                .body("[1].number", is(102))
                .body("[1].price", is(150.0f));
    }

    @Test
    void deleteRoom() {
        // Først tjekker at rummet findes
        given()
                .when()
                .get("/room/1")
                .then()
                .statusCode(200);

        // Sletter rummet
        given()
                .when()
                .delete("/room/1")
                .then()
                .statusCode(204); // NO_CONTENT

        // Tjekker at rummet ikke længere findes
        given()
                .when()
                .get("/room/1")
                .then()
                .statusCode(404);
    }


}
