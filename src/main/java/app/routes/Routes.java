package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private HotelRoutes hotelRoutes = new HotelRoutes();
    private RoomRoutes roomRoutes = new RoomRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            // root endpoint
            get("/", ctx -> ctx.result("Welcome to Hotel API!"));

            // endpoints
            path("/hotel", hotelRoutes.getRoutes());
            path("/room", roomRoutes.getRoutes());

        };
    }
}
