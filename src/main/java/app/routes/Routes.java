package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private HotelRoutes hotelRoutes = new HotelRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            // root endpoint
            get("/", ctx -> ctx.result("Welcome to Hotel API!"));

            // dog endpoints
            path("/hotel", hotelRoutes.getRoutes());

        };
    }
}
