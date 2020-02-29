package com.akvelon.cdp.exceptionslibrary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public abstract class RequestHandlingException extends RuntimeException {
    protected final int errorCode;
    protected final String message;
}
