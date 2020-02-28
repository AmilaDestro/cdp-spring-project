package com.akvelon.cdp.exceptionslibrary;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Parent exception class for not found entities in this application
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundException extends Throwable {
    protected final int errorCode;
    protected final String message;

    public NotFoundException(final int errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public NotFoundException(String message) {
        this.errorCode = 404;
        this.message = message;
    }
}
