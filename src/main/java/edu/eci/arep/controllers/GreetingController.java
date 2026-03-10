package edu.eci.arep.controllers;

import edu.eci.arep.annotations.GetMapping;
import edu.eci.arep.annotations.RequestParam;
import edu.eci.arep.annotations.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Demo REST controller that exposes a parameterized greeting endpoint.
 *
 * Registered automatically by MicroSpringBoot classpath scanning
 * and accessible at GET /greeting?name=World.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
@RestController
public class GreetingController {

    private static final String TEMPLATE = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /**
     * Handles GET requests to /greeting}, optionally accepting a name
     * query parameter to personalize the greeting.
     *
     * @param name the name to greet; defaults to "World" if not provided
     * @return a personalized greeting string prefixed with the current request count
     */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name + " (request #" + counter.incrementAndGet() + ")";
    }
}
