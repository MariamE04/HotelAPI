package app.config;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import app.routes.Routes;

import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationConfig {

    // opretter en SLF4J logger (bruges til at logge requests/responses og exceptions).
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class); // “Opret en logger med navnet = klassens navn (app.config.ApplicationConfig)”.

    // indeholder route-definitioner (hvor endpoints som GET/POST registreres).
    private static Routes routes = new Routes();

    public static void configuration(JavalinConfig config) {
        //slår den tekstuelle Javalin-banner ved opstart fra (renser konsollen).
        config.showJavalinBanner = false;

        // aktiverer et indbygget plugin som giver en oversigt over alle registrerede ruter på stien /routes (praktisk til debugging).
        config.bundledPlugins.enableRouteOverview("/routes");

        // sætter en base path for alle ruter; alle ruter vil få dette foranstillet
        config.router.contextPath = "/api/hotels";

        // OpenAPI
        config.registerPlugin(new OpenApiPlugin(openApiConfig -> {
            openApiConfig.documentationPath = "/openapi"; // JSON-dokumentation
        }));

        // Registrerer ruter i Javalin, bruger: "routes.getRoutes()" returnerer (typisk en lambda/consume: indeholder get(...), post(...) osv
        config.router.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        // Det svarer til: Javalin.create(config -> configuration(config)). Dvs. Javalin oprettes med den ovenstående konfiguration.
        var app = Javalin.create(ApplicationConfig::configuration);

        // log requests
        app.before(ctx -> { // en before-handler som kører før hver request-handler.
            logger.info("REQUEST | Method: {} | Path: {} | Body: {}",
                    ctx.method(), ctx.path(), ctx.body());
            // ctx.body() er request-body som string (kan være tom eller stor).
        });

        // log responses
        app.after(ctx -> { // after-handler: kører efter request-handler. Logger status, path og ctx.result() (response body). Koden sikrer at hvis ctx.result() er null så logges "empty".
            String result = ctx.result(); // er hvad min handler satte som response
            logger.info("RESPONSE | Status: {} | Path: {} | Result: {}",
                    ctx.status(), ctx.path(), result != null ? result : "empty");
        });

        // exception handling
        // logger IllegalStateException fejlen (inkl. stacktrace pga. , e) og sender HTTP 400 samt en JSON-body
        app.exception(IllegalStateException.class, (e, ctx) -> {
            logger.error("IllegalStateException | Path: {} | Message: {}", ctx.path(), e.getMessage(), e);
            ctx.status(400)
                    .json(Map.of(
                            "error", "Invalid input",
                            "message", e.getMessage()
                    ));
        });

        // En generel fallback-exception handler: logger og returnerer 500 + JSON med error og message
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Exception | Path: {} | Message: {}", ctx.path(), e.getMessage(), e);
            ctx.status(500)
                    .json(Map.of(
                            "error", "Something went wrong",
                            "message", e.getMessage()
                    ));
        });

        app.start(port); // starter Javalin på den givne port (binder socket og starter worker-tråde).
        return app; // returnerer Javalin-instansen (praktisk til tests, så man kan stoppe serveren bagefter eller foretage integrationstests).
    }

    public static void stopServer(Javalin app) {
        app.stop(); // stopper serveren.
        logger.info("Server stopped"); // log besked.
    }
}
