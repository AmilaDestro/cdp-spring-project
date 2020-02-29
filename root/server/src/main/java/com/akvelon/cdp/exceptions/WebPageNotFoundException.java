package com.akvelon.cdp.exceptions;

import static java.lang.String.format;

public class WebPageNotFoundException extends RequestHandlingException {

    public WebPageNotFoundException(final String url) {
        super(404, format("Wev Page was not found by URL %s", url));
    }
}
