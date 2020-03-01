package com.akvelon.cdp.services.greeting;

/**
 * Simple interface which contains only standard "Hello, World!" functionality
 */
public interface GreetingInterface {

    /**
     * Returns "Hello, <userName>!" or "Hello, World!" String
     * if userName wasn't specified
     *
     * @param userName of user to send greeting for
     * @return {@link String} with greeting for specified user
     */
    String sayHello(final String userName);
}
