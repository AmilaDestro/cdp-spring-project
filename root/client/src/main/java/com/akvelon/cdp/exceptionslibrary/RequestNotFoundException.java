package com.akvelon.cdp.exceptionslibrary;

/**
 * This exceptions is thrown when attempt to get/delete {@link com.akvelon.cdp.entities.Request}
 * is made
 */
public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(final long requestId) {
        super(String.format("Request entity with id %s was not found", requestId));
    }
}
