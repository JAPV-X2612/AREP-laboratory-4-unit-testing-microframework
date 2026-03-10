package edu.eci.arep.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an HTTP GET request to a specific URI path handled by the annotated method.
 *
 * Methods annotated with @GetMapping inside a RestController
 * class are registered in the ComponentRegistry and invoked by the
 * RequestRouter when a matching GET request is received.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {

    /**
     * The URI path this method handles.
     * @return the URI path string
     */
    String value();
}
