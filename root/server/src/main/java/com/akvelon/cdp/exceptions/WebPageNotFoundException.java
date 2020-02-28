package com.akvelon.cdp.exceptions;

public class WebPageNotFoundException extends NotFoundException {
    public WebPageNotFoundException(final String url) {
        super(String.format("Wev Page was not found by URL %s", url));
    }
}
