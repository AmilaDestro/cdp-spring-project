package com.akvelon.cdp.exceptionslibrary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Parent exception class for not found entities in this application
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class NotFoundException extends Throwable {
    protected final int errorCode;
    protected final String message;
}
