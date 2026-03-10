package edu.eci.arep.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds an HTTP query parameter to a method parameter in a GetMapping-annotated method.
 *
 * The RequestRouter reads this annotation via reflection to extract the
 * correct query string value and inject it when invoking the controller method.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {

    /**
     * The name of the query parameter to bind.
     * @return the query parameter name
     */
    String value();

    /**
     * The default value to use if the query parameter is absent from the request.
     * @return the default value string
     */
    String defaultValue() default "";
}
