package edu.eci.arep.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a REST controller component to be discovered and registered
 * by the IoC framework during classpath scanning at startup.
 *
 * Classes annotated with @RestController are instantiated once and
 * their GetMapping annotated methods are exposed as HTTP endpoints.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestController {
}
