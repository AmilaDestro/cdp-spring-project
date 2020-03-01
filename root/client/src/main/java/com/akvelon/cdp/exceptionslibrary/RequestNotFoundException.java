package com.akvelon.cdp.exceptionslibrary;

import static java.lang.String.format;

import com.akvelon.cdp.entitieslibrary.Request;

/**
 * This exceptions is thrown when attempt to get/delete {@link Request}
 * is made
 */
public class RequestNotFoundException extends RequestHandlingException {

    public RequestNotFoundException(final long requestId) {
        super(404, format("Internal request entity with id %s was not found", requestId));
    }
}
