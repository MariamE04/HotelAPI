package app.config;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import app.routes.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private static Routes routes = new Routes();

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes");
        config.router.contextPath = "/api/hotels";
        config.router.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        var app = Javalin.create(ApplicationConfig::configuration);

        // log requests
        app.before(ctx -> {
            logger.info("REQUEST | Method: {} | Path: {} | Body: {}",
                    ctx.method(), ctx.path(), ctx.body());
        });

        // log responses
        app.after(ctx -> {
            String result = ctx.result();
            logger.info("RESPONSE | Status: {} | Path: {} | Result: {}",
                    ctx.status(), ctx.path(), result != null ? result : "empty");
        });

        // exception handling
        app.exception(IllegalStateException.class, (e, ctx) -> {
            logger.error("IllegalStateException | Path: {} | Message: {}", ctx.path(), e.getMessage(), e);
            ctx.status(400)
                    .json(Map.of(
                            "error", "Invalid input",
                            "message", e.getMessage()
                    ));
        });

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Exception | Path: {} | Message: {}", ctx.path(), e.getMessage(), e);
            ctx.status(500)
                    .json(Map.of(
                            "error", "Something went wrong",
                            "message", e.getMessage()
                    ));
        });

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
        logger.info("Server stopped");
    }
}
