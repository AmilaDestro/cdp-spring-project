package com.akvelon.cdp.services.greeting;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

/**
 * Service which says hello to a user
 */
@Component
@Slf4j
public class GreetingService implements GreetingInterface {

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * {@inheritDoc}
     */
    @Override
    public String sayHello(final String userName) {
        log.debug("User {} will receive greeting in 10 seconds", userName);
        return sayHelloWith10SecondsDelay(userName);
    }

    /**
     * Waits for 10 seconds before sending greeting message to user.
     * Actual delay may differ if exceptions are thrown during the method execution
     *
     * @param userToGreet - name of user to greet
     * @return greeting {@link String}
     */
    private String sayHelloWith10SecondsDelay(final String userToGreet) {
        final long startDelay = System.currentTimeMillis();

        try {
            val greetingToReturn = executorService.schedule(() -> greeting(userToGreet.trim()), 10, SECONDS).get();

            final long endDelay = System.currentTimeMillis() - startDelay;
            log.debug("Greeting to user {} was actually sent with delay: {} seconds", userToGreet,
                      MILLISECONDS.toSeconds(endDelay));

            return greetingToReturn;

        } catch (InterruptedException | ExecutionException e) {
            val currentClass = this.getClass();
            log.error("Exception occurred during {}.{}", currentClass.getName(), currentClass.getMethods()[0]);
        }

        final long endDelay = System.currentTimeMillis() - startDelay;
        log.debug("Greeting to user {} was actually sent with delay: {} seconds", userToGreet,
                  MILLISECONDS.toSeconds(endDelay));
        return greeting(userToGreet);
    }

    /**
     * Return greeting message to user
     *
     * @param userName of user
     * @return {@link String}
     */
    private String greeting(String userName) {
        if (userName == null || userName.isBlank()) {
            userName = "World";
        }
        return format("Hello %s", userName);
    }
}
