import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.entities.Hotel;
import app.entities.Room;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // fortæller JUnit, at den skal lave én enkelt instans af testklassen, og bruge den til alle tests.
public class HotelControllerTest {

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
            em.createQuery("DELETE FROM Hotel").executeUpdate();

            Hotel h1 = Hotel.builder()
                    .name("Hotel 1")
                    .address("Address 1")
                    .build();

            Room r1 = new Room(null, h1, 101, 100.0);
            Room r2 = new Room(null, h1, 102, 150.0);

            h1.setRooms(new ArrayList<>(List.of(r1, r2)));

            em.persist(h1);

            Hotel h2 = Hotel.builder()
                    .name("Hotel 1")
                    .address("Address 1")
                    .build();

            Room r3 = new Room(null, h2, 140, 104.0);
            Room r4 = new Room(null, h2, 150, 103.0);

            h2.setRooms(new ArrayList<>(List.of(r3, r4)));

            em.persist(h2);


            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Test
    void getHotelById(){
        given()
                .when()
                .get("/hotel/1")
                .then()
                .statusCode(200)
                .body("name", is("Hotel 1") )
                .body("address", is("Address 1"))
                .body("rooms[0].number", is(101))
                .body("rooms[0].price", is(100.0f)) // vigtigt: float matcher kræver `f`
                .body("rooms[1].number", is(102))
                .body("rooms[1].price", is(150.0f));
    }

    @Test
    void getAllHotels(){
        given()
                .when()
                .get("/hotel")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    void testPostHotel(){
        String newHotel= """
                  {
                  "name": "Hotel 9", 
                  "address": "Address 9"
                  }
                                 
                """;

        given()
                .contentType("application/json")
                .body(newHotel)
                .when()
                .post("/hotel")
                .then()
                .statusCode(201)
                .body("name", is("Hotel 9"))
                .body("address", is("Address 9"));
    }


    @Test
    void testPutHotel(){
        String updatedHotel = """
                 {
                  "name": "Hotel 1", 
                  "address": "test 9"
                  }
                """;

        given()
                .contentType("application/json")
                .body(updatedHotel)
                .when()
                .put("/hotel/1")
                .then()
                .statusCode(200)
                .body("address", containsString("test 9"));

    }

    @Test
    void testDelete(){
        given()
                .when()
                .delete("/hotel/1")
                .then()
                .statusCode(204);

        // check it is deleted
        given()
                .when()
                .get("/hotel/1")
                .then()
                .statusCode(404);
    }




}
