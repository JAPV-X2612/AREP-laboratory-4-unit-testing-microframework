package edu.eci.arep.framework;

import edu.eci.arep.annotations.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatches incoming HTTP GET requests to the appropriate controller method
 * registered in the ComponentRegistry.
 *
 * The router parses the URI and query string, resolves RequestParam
 * annotations via reflection to inject arguments, and returns the String
 * result produced by the invoked method.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
public class RequestRouter {

    private final ComponentRegistry registry;

    /**
     * Constructs a RequestRouter backed by the given ComponentRegistry.
     * @param registry the component registry to use for route resolution
     */
    public RequestRouter(ComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * Handles an incoming request line (e.g., "GET /greeting?name=World HTTP/1.1")
     * by locating the registered handler and invoking it with resolved parameters.
     *
     * @param requestLine the raw first line of the HTTP request
     * @return the String body returned by the controller method,
     *         or an error message if no handler is found or invocation fails
     */
    public String route(String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            return "400 Bad Request";
        }

        String fullPath = parts[1];
        String path;
        Map<String, String> queryParams = new HashMap<>();

        if (fullPath.contains("?")) {
            String[] split = fullPath.split("\\?", 2);
            path = split[0];
            queryParams = parseQueryString(split[1]);
        } else {
            path = fullPath;
        }

        if (!registry.hasRoute(path)) {
            return null;
        }

        Method method = registry.getMethod(path);
        Object instance = registry.getInstance(path);

        try {
            Object[] args = resolveArguments(method, queryParams);
            return (String) method.invoke(instance, args);
        } catch (Exception e) {
            return "500 Internal Server Error: " + e.getMessage();
        }
    }

    /**
     * Resolves the arguments to pass to a controller method by inspecting each parameter's
     * RequestParam annotation and matching it against the provided query parameters map.
     *
     * @param method        the controller method whose parameters are to be resolved
     * @param queryParams   the parsed query string parameters from the request URI
     * @return an array of resolved argument values ready for method invocation
     */
    private Object[] resolveArguments(Method method, Map<String, String> queryParams) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = parameters[i].getAnnotation(RequestParam.class);
                String paramName = annotation.value();
                String defaultVal = annotation.defaultValue();
                args[i] = queryParams.getOrDefault(paramName, defaultVal);
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    /**
     * Parses a query string (e.g., "name=World&lang=en") into a key-value map.
     *
     * @param queryString the raw query string without the leading ?
     * @return a Map of parameter names to their decoded string values
     */
    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            } else if (kv.length == 1) {
                params.put(kv[0], "");
            }
        }
        return params;
    }
}
