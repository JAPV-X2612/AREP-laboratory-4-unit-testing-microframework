package edu.eci.arep;

import edu.eci.arep.annotations.RestController;
import edu.eci.arep.framework.ComponentRegistry;
import edu.eci.arep.framework.RequestRouter;
import edu.eci.arep.server.HttpServer;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Entry point and bootstrap class for the IoC Web Framework.
 *
 * Performs classpath scanning to discover all classes annotated with RestController,
 * registers them in a ComponentRegistry, and starts the HttpServer.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
public class MicroSpringBoot {

    private static final String BASE_PACKAGE = "edu.eci.arep";

    /**
     * Application entry point. Scans for controllers, populates the registry,
     * and starts the HTTP server.
     *
     * @param args optional fully-qualified class name of a specific controller to load
     */
    public static void main(String[] args) {
        ComponentRegistry registry = new ComponentRegistry();

        if (args.length > 0) {
            loadExplicitController(args[0], registry);
        } else {
            scanAndRegisterAll(registry);
        }

        RequestRouter router = new RequestRouter(registry);
        HttpServer server = new HttpServer(router);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("[MicroSpringBoot] Fatal error starting server: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Loads and registers a single controller class specified by its fully-qualified name.
     *
     * @param className the fully-qualified name of the controller class to load
     * @param registry  the ComponentRegistry to register the controller into
     * @throws RuntimeException if the class cannot be found on the classpath
     */
    private static void loadExplicitController(String className, ComponentRegistry registry) {
        try {
            Class<?> controllerClass = Class.forName(className);
            registry.registerControllers(Set.of(controllerClass));
            System.out.println("[MicroSpringBoot] Loaded explicit controller: " + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[MicroSpringBoot] Controller class not found: " + className, e);
        }
    }

    /**
     * Scans the #BASE_PACKAGE for all classes annotated with
     * RestController and registers them all in the given registry.
     *
     * @param registry the componentRegistry to populate
     */
    private static void scanAndRegisterAll(ComponentRegistry registry) {
        System.out.println("[MicroSpringBoot] Scanning package: " + BASE_PACKAGE);
        Reflections reflections = new Reflections(BASE_PACKAGE);
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);
        System.out.println("[MicroSpringBoot] Found " + controllers.size() + " controller(s).");
        registry.registerControllers(controllers);
    }
}
