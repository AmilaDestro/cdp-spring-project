package com.akvelon.cdp.exceptions;

import static java.lang.String.format;

/**
 * This exceptions is thrown when attempt to get/delete {@link com.akvelon.cdp.entities.Request}
 * is made
 */
public class RequestNotFoundException extends RequestHandlingException {

    public RequestNotFoundException(final long requestId) {
        super(404, format("Internal request entity with id %s was not found", requestId));
    }
}
