package app.routes;

import app.controllers.HotelController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;


public class HotelRoutes {
   HotelController hotelController = new HotelController();

   public EndpointGroup getRoutes() {
        return () -> {
            get(hotelController::getAllHotels);
            post(hotelController::createHotel);
            path("/{id}", () -> {
                get(hotelController::getHotelById);
                put(hotelController::updateHotel);
                delete(hotelController::hotelToDelete);
            });

        };
    }
}