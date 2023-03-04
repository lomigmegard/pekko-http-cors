package ch.megard.pekko.http.cors.javadsl.model;

import org.apache.pekko.http.impl.util.Util;
import ch.megard.pekko.http.cors.scaladsl.model.HttpHeaderRange$;


/**
 * @see HttpHeaderRanges for convenience access to often used values.
 */
public abstract class HttpHeaderRange {
    public abstract boolean matches(String header);

    public static HttpHeaderRange create(String... headers) {
        return HttpHeaderRange$.MODULE$.apply(Util.convertArray(headers));
    }
}
