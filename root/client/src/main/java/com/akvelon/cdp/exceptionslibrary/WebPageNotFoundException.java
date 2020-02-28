package com.akvelon.cdp.exceptionslibrary;

public class WebPageNotFoundException extends NotFoundException {
    public WebPageNotFoundException(final String url) {
        super(String.format("Wev Page was not found by URL %s", url));
    }
}
