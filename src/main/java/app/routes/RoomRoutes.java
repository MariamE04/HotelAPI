package app.routes;

import app.controllers.RoomController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class RoomRoutes {

    RoomController roomController = new RoomController();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/{hotelId}/rooms", () -> {
                get(roomController::getRoomsForHotel);
            });

            path("/{id}", () -> {
                get(roomController::getRoomById);
                delete(roomController::roomToDelete);
            });

        };
    }

}
