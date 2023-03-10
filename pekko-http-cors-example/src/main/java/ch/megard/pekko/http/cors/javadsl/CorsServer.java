package ch.megard.pekko.http.cors.javadsl;


import org.apache.pekko.http.javadsl.model.StatusCodes;
import org.apache.pekko.http.javadsl.server.*;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

import static ch.megard.pekko.http.cors.javadsl.CorsDirectives.cors;
import static ch.megard.pekko.http.cors.javadsl.CorsDirectives.corsRejectionHandler;

/**
 * Example of a Java HTTP server using the CORS directive.
 */
public class CorsServer extends HttpApp {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final CorsServer app = new CorsServer();
        app.startServer("127.0.0.1", 9000);
    }

    protected Route routes() {

        // Your CORS settings are loaded from `application.conf`

        // Your rejection handler
        final RejectionHandler rejectionHandler = corsRejectionHandler().withFallback(RejectionHandler.defaultHandler());

        // Your exception handler
        final ExceptionHandler exceptionHandler = ExceptionHandler.newBuilder()
                .match(NoSuchElementException.class, ex -> complete(StatusCodes.NOT_FOUND, ex.getMessage()))
                .build();

        // Combining the two handlers only for convenience
        final Function<Supplier<Route>, Route> handleErrors = inner -> Directives.allOf(
                s -> handleExceptions(exceptionHandler, s),
                s -> handleRejections(rejectionHandler, s),
                inner
        );

        // Note how rejections and exceptions are handled *before* the CORS directive (in the inner route).
        // This is required to have the correct CORS headers in the response even when an error occurs.
        return handleErrors.apply(() -> cors(() -> handleErrors.apply(() -> concat(
                path("ping", () -> complete("pong")),
                path("pong", () -> failWith(new NoSuchElementException("pong not found, try with ping")))
        ))));
    }

}
