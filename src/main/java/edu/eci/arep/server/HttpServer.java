package edu.eci.arep.server;

import edu.eci.arep.framework.ComponentRegistry;
import edu.eci.arep.framework.RequestRouter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Single-threaded HTTP/1.1 server that handles non-concurrent client connections,
 * serves static files (HTML, CSS, JS, PNG) from the classpath, and delegates
 * REST API requests to the RequestRouter.
 *
 * Static resources are resolved from /static/ on the classpath.
 * REST endpoints registered in the ComponentRegistry are served under
 * the /api prefix or directly by URI path, depending on routing rules.
 *
 * @author Jesús Pinzón
 * @version 1.0
 * @since 2026-03-04
 */
public class HttpServer {

    private static final int PORT = 35000;
    private static final String STATIC_BASE = "/static";
    private final RequestRouter router;
    private boolean running = true;

    /**
     * Constructs an HttpServer with the given RequestRouter.
     * @param router the request router backed by a populated ComponentRegistry
     */
    public HttpServer(RequestRouter router) {
        this.router = router;
    }

    /**
     * Starts the server and enters the main accept loop, handling one client
     * connection at a time until running is set to false.
     *
     * @throws IOException if the ServerSocket cannot be opened on #PORT
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[HttpServer] Listening on port " + PORT);
            while (running) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("[HttpServer] Error handling client: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reads the HTTP request from the client socket, determines whether it is
     * a REST call or a static file request, and writes the appropriate response.
     *
     * @param clientSocket the accepted client Socket
     * @throws IOException if reading from or writing to the socket fails
     */
    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        System.out.println("[HttpServer] " + requestLine);

        String path = extractPath(requestLine);

        // Attempt REST route first
        String restResponse = router.route(requestLine);
        if (restResponse != null) {
            String httpResponse = HttpResponse.ok("text/plain", restResponse);
            out.write(httpResponse.getBytes());
            out.flush();
            return;
        }

        // Fall back to static file serving
        serveStaticFile(path, out);
    }

    /**
     * Resolves and serves a static resource from the classpath under /static/.
     * Supports .html, .css, .js, and .png files.
     *
     * @param path the requested URI path (e.g., "/index.html")
     * @param out  the output stream of the client socket
     * @throws IOException if writing to the output stream fails
     */
    private void serveStaticFile(String path, OutputStream out) throws IOException {
        String resourcePath = STATIC_BASE + (path.equals("/") ? "/index.html" : path);
        InputStream resourceStream = getClass().getResourceAsStream(resourcePath);

        if (resourceStream == null) {
            out.write(HttpResponse.notFound(path).getBytes());
            out.flush();
            return;
        }

        byte[] body = resourceStream.readAllBytes();
        String contentType = resolveContentType(path);

        if (contentType.startsWith("image/")) {
            out.write(HttpResponse.okBinary(contentType, body).getBytes());
            out.write(body);
        } else {
            out.write(HttpResponse.ok(contentType, new String(body)).getBytes());
        }
        out.flush();
    }

    /**
     * Extracts the URI path from the raw HTTP request line, stripping the method
     * and protocol version.
     *
     * @param requestLine the raw first line of the HTTP request (e.g., "GET /index.html HTTP/1.1")
     * @return the URI path string (e.g., "/index.html"), or "/" if parsing fails
     */
    private String extractPath(String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) return "/";
        String full = parts[1];
        return full.contains("?") ? full.split("\\?")[0] : full;
    }

    /**
     * Resolves the MIME content type for a given file path based on its extension.
     *
     * @param path the file path or URI (e.g., "/image.png")
     * @return the MIME type string (defaults to "application/octet-stream" for unknown types)
     */
    private String resolveContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
