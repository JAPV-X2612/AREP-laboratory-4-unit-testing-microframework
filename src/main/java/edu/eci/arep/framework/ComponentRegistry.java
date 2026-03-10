package edu.eci.arep.framework;

import edu.eci.arep.annotations.GetMapping;
import edu.eci.arep.annotations.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry that stores the mapping between URI paths and the controller methods
 * that handle them, built by scanning classes annotated with RestController.
 *
 * On registration, each RestController-annotated class is instantiated
 * once (singleton per scan), and every GetMapping annotated method within
 * it is stored as a URI → MethodHandle entry.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
public class ComponentRegistry {

    private final Map<String, Method> routeMethodMap = new HashMap<>();
    private final Map<String, Object> routeInstanceMap = new HashMap<>();

    /**
     * Scans the provided set of classes, instantiates those annotated with
     * RestController, and registers all their GetMapping} methods.
     *
     * @param controllers the set of classes to inspect and register
     * @throws RuntimeException if any controller class cannot be instantiated
     */
    public void registerControllers(Set<Class<?>> controllers) {
        for (Class<?> controllerClass : controllers) {
            if (!controllerClass.isAnnotationPresent(RestController.class)) {
                continue;
            }
            try {
                Object instance = controllerClass.getDeclaredConstructor().newInstance();
                for (Method method : controllerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        String uri = method.getAnnotation(GetMapping.class).value();
                        routeMethodMap.put(uri, method);
                        routeInstanceMap.put(uri, instance);
                        System.out.println("[ComponentRegistry] Registered GET " + uri
                                + " -> " + controllerClass.getSimpleName() + "#" + method.getName());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "[ComponentRegistry] Failed to instantiate controller: "
                                + controllerClass.getName(), e);
            }
        }
    }

    /**
     * Retrieves the method registered for the given URI path.
     *
     * @param uri the URI path to look up
     * @return the Method mapped to the URI, or null} if not found
     */
    public Method getMethod(String uri) {
        return routeMethodMap.get(uri);
    }

    /**
     * Retrieves the controller instance registered for the given URI path.
     *
     * @param uri the URI path to look up
     * @return the controller instance mapped to the URI, or null if not found
     */
    public Object getInstance(String uri) {
        return routeInstanceMap.get(uri);
    }

    /**
     * Checks whether a given URI path has a registered handler.
     *
     * @param uri the URI path to check
     * @return true if a handler is registered, false otherwise
     */
    public boolean hasRoute(String uri) {
        return routeMethodMap.containsKey(uri);
    }
}
