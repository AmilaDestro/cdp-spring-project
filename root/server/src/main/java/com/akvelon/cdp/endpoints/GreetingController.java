package com.akvelon.cdp.endpoints;

import com.akvelon.cdp.services.greeting.GreetingInterface;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Rest controller which provides method greeting methods
 */

@RequiredArgsConstructor
@RestController
public class GreetingController {
    @NonNull
    private GreetingInterface greeting;

    /**
     * Returns greeting String "Hello, <name>" or "Hello, World" if <name> was not specified
     *
     * @param name - of user which is passed as request query parameter
     * @return {@link String}
     */
    @RequestMapping(value = "/hello", method = GET)
    public String greeting(@RequestParam(value="name", required=false, defaultValue = "World")final String name) {
        return greeting.sayHello(name);
    }
}
