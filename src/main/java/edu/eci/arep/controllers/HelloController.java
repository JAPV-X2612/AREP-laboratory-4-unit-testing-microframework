package edu.eci.arep.controllers;

import edu.eci.arep.annotations.GetMapping;
import edu.eci.arep.annotations.RestController;

/**
 * Demo REST controller that exposes a root endpoint returning a static greeting.
 *
 * Registered automatically by MicroSpringBoot classpath scanning and accessible at GET /.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
@RestController
public class HelloController {

    /**
     * Handles GET requests to the root path /
     * @return a static greeting string
     */
    @GetMapping("/")
    public String index() {
        return "Greetings from MicroSpringBoot!";
    }
}
