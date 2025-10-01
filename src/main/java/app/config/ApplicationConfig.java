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

    private static ApplicationConfig instance;
    private Javalin app;

    private ApplicationConfig() {}

    public static ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    public static void configuration(JavalinConfig config) {
        //slår den tekstuelle Javalin-banner ved opstart fra (renser konsollen).
        config.showJavalinBanner = false;

        // aktiverer et indbygget plugin som giver en oversigt over alle registrerede ruter på stien /routes (praktisk til debugging).
        config.bundledPlugins.enableRouteOverview("/routes");

        // sætter en base path for alle ruter; alle ruter vil få dette foranstillet
        config.router.contextPath = "/api/hotels";

        config.http.defaultContentType = "application/json";

        // OpenAPI
        config.registerPlugin(new OpenApiPlugin(openApiConfig -> {
            openApiConfig.documentationPath = "/openapi"; // JSON-dokumentation
        }));

        // Registrerer ruter i Javalin, bruger: "routes.getRoutes()" returnerer (typisk en lambda/consume: indeholder get(...), post(...) osv
        config.router.apiBuilder(routes.getRoutes());
    }

    public Javalin startServer(int port) {

        if (this.app != null) {
            logger.warn("Server is already running on port {}", this.app.port());
            return this.app;
        }

        // Det svarer til: Javalin.create(config -> configuration(config)). Dvs. Javalin oprettes med den ovenstående konfiguration.
        this.app = Javalin.create(ApplicationConfig::configuration);

        // log requests
        this.app.before(ctx -> { // en before-handler som kører før hver request-handler.
            logger.info("REQUEST | Method: {} | Path: {} | Body: {}",
                    ctx.method(), ctx.path(), ctx.body());
            // ctx.body() er request-body som string (kan være tom eller stor).
        });

        // log responses
        this.app.after(ctx -> { // after-handler: kører efter request-handler. Logger status, path og ctx.result() (response body). Koden sikrer at hvis ctx.result() er null så logges "empty".
            String result = ctx.result(); // er hvad min handler satte som response
            logger.info("RESPONSE | Status: {} | Path: {} | Result: {}",
                    ctx.status(), ctx.path(), result != null ? result : "empty");
        });

        // CORS
        this.app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");
        });
        this.app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");
        });

        // exception handling
        // logger IllegalStateException fejlen (inkl. stacktrace pga. , e) og sender HTTP 400 samt en JSON-body
        this.app.exception(IllegalStateException.class, (e, ctx) -> {
            logger.error("IllegalStateException | Path: {} | Message: {}", ctx.path(), e.getMessage(), e);
            ctx.status(400)
                    .json(Map.of(
                            "error", "Invalid input",
                            "message", e.getMessage()
                    ));
        });

        // En generel fallback-exception handler: logger og returnerer 500 + JSON med error og message
        this.app.exception(Exception.class, (e, ctx) -> {
            logger.error("Exception | Path: {} | Message: {}", ctx.path(), e.getMessage(), e);
            ctx.status(500)
                    .json(Map.of(
                            "error", "Something went wrong",
                            "message", e.getMessage()
                    ));
        });

        this.app.start(port); // starter Javalin på den givne port (binder socket og starter worker-tråde).
        return app; // returnerer Javalin-instansen (praktisk til tests, så man kan stoppe serveren bagefter eller foretage integrationstests).
    }

    public Javalin getApp() {
        return app;
    }


    public void stopServer() {
        if (this.app != null) {
            this.app.stop();
            logger.info("Server stopped");
        }
    }
}
