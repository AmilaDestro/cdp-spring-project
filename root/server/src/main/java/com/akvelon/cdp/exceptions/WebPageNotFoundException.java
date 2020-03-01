package com.akvelon.cdp.exceptions;

import static java.lang.String.format;

/**
 * This exception is thrown when attempt to get requested web page was made but this
 * page does not exist or request execution failed
 */
public class WebPageNotFoundException extends RequestHandlingException {

    public WebPageNotFoundException(final String url) {
        super(404, format("Wev Page was not found by URL %s", url));
    }
}
