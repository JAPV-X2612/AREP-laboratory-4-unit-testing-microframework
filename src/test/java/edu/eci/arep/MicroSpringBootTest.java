package edu.eci.arep;

import edu.eci.arep.controllers.GreetingController;
import edu.eci.arep.controllers.HelloController;
import edu.eci.arep.framework.ComponentRegistry;
import edu.eci.arep.framework.RequestRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the core IoC framework components: ComponentRegistry and RequestRouter.
 *
 * Verifies that controller registration, route resolution, and query parameter
 * injection work correctly without starting an actual HTTP server.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-05
 */
class MicroSpringBootTest {

    private ComponentRegistry registry;
    private RequestRouter router;

    @BeforeEach
    void setUp() {
        registry = new ComponentRegistry();
        registry.registerControllers(Set.of(HelloController.class, GreetingController.class));
        router = new RequestRouter(registry);
    }

    @Test
    void testRootEndpointRegistered() {
        assertTrue(registry.hasRoute("/"), "Route '/' should be registered");
    }

    @Test
    void testGreetingEndpointRegistered() {
        assertTrue(registry.hasRoute("/greeting"), "Route '/greeting' should be registered");
    }

    @Test
    void testUnknownRouteReturnsNull() {
        assertNull(router.route("GET /unknown HTTP/1.1"), "Unknown route should return null");
    }

    @Test
    void testRootRouteResponse() {
        String response = router.route("GET / HTTP/1.1");
        assertNotNull(response);
        assertTrue(response.contains("Greetings"), "Response should contain 'Greetings'");
    }

    @Test
    void testGreetingDefaultParam() {
        String response = router.route("GET /greeting HTTP/1.1");
        assertNotNull(response);
        assertTrue(response.contains("World"), "Default name should be 'World'");
    }

    @Test
    void testGreetingCustomParam() {
        String response = router.route("GET /greeting?name=AREP HTTP/1.1");
        assertNotNull(response);
        assertTrue(response.contains("AREP"), "Custom name 'AREP' should appear in response");
    }
}
