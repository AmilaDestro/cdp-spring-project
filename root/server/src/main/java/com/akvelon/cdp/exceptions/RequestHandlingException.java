package com.akvelon.cdp.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Basic exception class that will be extended by other exceptions in this application
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public abstract class RequestHandlingException extends RuntimeException {
    protected final int errorCode;
    protected final String message;
}
